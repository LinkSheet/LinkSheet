package fe.linksheet.module.analytics

import androidx.lifecycle.LifecycleCoroutineScope
import fe.linksheet.module.log.Logger
import fe.linksheet.module.network.NetworkStateService
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import java.util.*
import kotlin.math.pow

class BatchedEventQueue(
    private val client: AnalyticsClient,
    private val coroutineScope: LifecycleCoroutineScope,
    val logger: Logger,
    private var currentLevel: TelemetryLevel?,
    private val networkState: NetworkStateService,
) {
    private var job: Job? = null
    private val eventQueue = Channel<AnalyticsEvent>(capacity = UNLIMITED)

    companion object {
        // TODO: The client should define these settings

        // At most 25 (https://github.com/aptabase/aptabase/blob/06c026505f1a91b9ddb4717838a8f8132d830fcb/src/Features/Ingestion/EventsController.cs#L95)
        const val CHUNK_SIZE = 5
        const val MAX_CHUNK_SIZE = 25
        const val BATCHING_TIMEOUT_MILLIS = 15 * 1000L
        const val SEND_TRIES = 5
        val TRY_DELAY: (Int) -> Long = { attemptNo -> 10 * 1000L * 2.0.pow(attemptNo).toLong() }
    }

    private fun createEventProcessor(level: TelemetryLevel?, dispatcher: CoroutineDispatcher = Dispatchers.IO): Job? {
        if (level == null) return null
//        if (!supported || !checkImplEnabled()) return null
        if (level == TelemetryLevel.Disabled) return null

        return coroutineScope.launch(dispatcher) { processEvents() }
    }

    fun startWith(newLevel: TelemetryLevel?) {
        logger.debug("Starting client, currentLevel: $currentLevel, newLevel: $newLevel")
        currentLevel = newLevel

        coroutineScope.launch { start() }
    }

    suspend fun start() {
        logger.debug("Active processor: $job, cancelling")

        job?.cancelAndJoin()

        logger.debug("Creating event processor with level $currentLevel")
        job = createEventProcessor(currentLevel)
    }

    suspend fun stop() {
        logger.debug("Stop received, cancelling processor")

        job?.cancelAndJoin()
        logger.debug("Cancelled, have ${batchedEvents.size} batched events")

        val chunks = batchedEvents.chunked(MAX_CHUNK_SIZE)
        logger.debug("Split batched events into ${chunks.size} chunks")

        for (chunk in chunks) {
            send(chunk)
        }
    }

    private val batchedEvents = LinkedList<AnalyticsEvent>()

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun processEvents() {
        logger.debug("Process event loop started")
        var lastSend = -1L

        while (!eventQueue.isClosedForReceive && currentCoroutineContext().isActive) {
            batchedEvents.clear()

            val firstEvent = eventQueue.receive()
            logger.debug("Received $firstEvent (first)")
            batchedEvents.add(firstEvent)

            val timeoutExceeded = withTimeoutOrNull(BATCHING_TIMEOUT_MILLIS) {
                repeat(CHUNK_SIZE - 1) {
                    val additionalEvent = eventQueue.receive()
                    logger.debug("Received $additionalEvent (additional)")
                    batchedEvents.add(additionalEvent)
                }

                false
            } ?: true

            logger.debug("Batched ${batchedEvents.size} events, timeout exceeded: $timeoutExceeded")

            if (!timeoutExceeded && lastSend != -1L) {
                val diff = System.currentTimeMillis() - lastSend
                val waitMillis = BATCHING_TIMEOUT_MILLIS - diff
                if (waitMillis > 0) {
                    delay(waitMillis)
                }
            }

            logger.debug("Sending events (${batchedEvents.size}, timeout: $timeoutExceeded)")
            send(batchedEvents)

            lastSend = System.currentTimeMillis()
        }
    }

    private suspend fun send(
        events: List<AnalyticsEvent>,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = withContext(dispatcher) {
        if (events.isEmpty()) return@withContext false

        logger.debug("Awaiting internet access..")
        networkState.awaitNetworkConnection()
        logger.debug("Internet connection available")

        for (i in 0 until SEND_TRIES) {
            logger.debug("Trying to send events (attemptNo: ${i + 1})")
            runCatching {
                val success = client.sendEvents(events)
                logger.debug("Send result: $success")
                if (success) {
                    return@withContext true
                }
            }.onFailure {
                it.printStackTrace()
                // TODO: Better exception logging
                logger.error("Failed to send event", it)
            }

            delay(TRY_DELAY(i))
        }

        false
    }

    fun enqueue(event: AnalyticsEvent?): Boolean {
        if (event == null) return false
        if (currentLevel?.canSendEvent(event) == false) return false

        val result = eventQueue.trySend(event)
        logger.debug("Enqueuing $event: ${result.isSuccess}")

        return result.isSuccess
    }
}
