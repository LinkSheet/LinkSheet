package fe.linksheet.module.analytics

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import fe.linksheet.BuildConfig
import fe.linksheet.extension.koin.service
import fe.linksheet.lifecycle.Service
import fe.linksheet.module.analytics.client.AptabaseAnalyticsClient
import fe.linksheet.module.log.Logger
import fe.linksheet.module.network.NetworkStateService
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import org.koin.dsl.module
import java.io.IOException
import java.util.*
import kotlin.math.pow
import kotlin.properties.Delegates

@OptIn(SensitivePreference::class)
val analyticsModule = module {
    service<AnalyticsClient, AppPreferenceRepository, NetworkStateService> { _, preferences, networkState ->
        val id = preferences.getOrPutInit(AppPreferences.telemetryId)
        val identity = preferences.get(AppPreferences.telemetryIdentity)
        val level = preferences.get(AppPreferences.telemetryLevel)
        val hasMadeChoice = !preferences.get(AppPreferences.telemetryShowInfoDialog)

        AptabaseAnalyticsClient(
            BuildConfig.ANALYTICS_SUPPORTED,
            applicationLifecycle.coroutineScope,
            identity.create(applicationContext, id),
            if (hasMadeChoice) level else null,
            networkState,
            logger,
            BuildConfig.APTABASE_API_KEY
        )
    }
}

abstract class AnalyticsClient(
    private val supported: Boolean,
    private val coroutineScope: LifecycleCoroutineScope,
    protected val identityData: TelemetryIdentityData,
    initialLevel: TelemetryLevel? = null,
    private val networkState: NetworkStateService,
    val logger: Logger,
) : Service {
    private var currentLevel: TelemetryLevel? = initialLevel

    private var eventProcessor: Job? = null
    private var enabled by Delegates.notNull<Boolean>()

    private val eventQueue = Channel<AnalyticsEvent>(capacity = UNLIMITED)

    companion object {
        // At most 25 (https://github.com/aptabase/aptabase/blob/06c026505f1a91b9ddb4717838a8f8132d830fcb/src/Features/Ingestion/EventsController.cs#L95)
        const val CHUNK_SIZE = 5
        const val MAX_CHUNK_SIZE = 25
        const val BATCHING_TIMEOUT_MILLIS = 15 * 1000L
        const val SEND_TRIES = 5
        val TRY_DELAY: (Int) -> Long = { attemptNo -> 10 * 1000L * 2.0.pow(attemptNo).toLong() }
    }

    protected open fun checkImplEnabled() = true

    protected open fun setup(context: Context) {}

    @Throws(IOException::class)
    protected abstract fun send(events: List<AnalyticsEvent>): Boolean

    override fun onAppInitialized(lifecycle: Lifecycle) {
        if (currentLevel != null) {
            tryStart()
        }
    }

    fun updateLevel(newLevel: TelemetryLevel): AnalyticsClient {
        currentLevel = newLevel
        return this
    }

    fun tryStart(): Boolean {
        if (!supported || !this.checkImplEnabled()) return false
        if (currentLevel == TelemetryLevel.Disabled) return false

        eventProcessor = coroutineScope.launch(Dispatchers.IO) {
            processEvents()
        }

        return true
    }

    override fun onStop(lifecycle: Lifecycle) {
        logger.debug("Stop received, cancelling processor")

        coroutineScope.launch(Dispatchers.IO) {
            eventProcessor?.cancelAndJoin()
            logger.debug("Cancelled, have ${batchedEvents.size} batched events")

            val chunks = batchedEvents.chunked(MAX_CHUNK_SIZE)
            logger.debug("Split batched events into ${chunks.size} chunks")

            for (chunk in chunks) {
                trySend(chunk)
            }
        }
    }

    private val batchedEvents = LinkedList<AnalyticsEvent>()

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun processEvents() {
        var lastSend = -1L

        while (!eventQueue.isClosedForReceive) {
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
            trySend(batchedEvents)

            lastSend = System.currentTimeMillis()
        }
    }

    private suspend fun trySend(events: List<AnalyticsEvent>): Boolean {
        if (events.isEmpty()) return false

        logger.debug("Awaiting internet access..")
        networkState.awaitNetworkConnection()
        logger.debug("Internet connection available")

        for (i in 0 until SEND_TRIES) {
            logger.debug("Trying to send events (attemptNo: ${i + 1})")
            runCatching {
                val success = send(events)
                logger.debug("Send result: $success")
                if (success) {
                    return true
                }
            }.onFailure {
                it.printStackTrace()
                // TODO: Better exception logging
                logger.error("Failed to send event", it)
            }

            delay(TRY_DELAY(i))
        }

        return false
    }

    fun enqueue(event: AnalyticsEvent?): Boolean {
        if (event == null) return false
        if (currentLevel?.canSendEvent(event) == false) return false

        val result = eventQueue.trySend(event)
        logger.debug("Enqueuing $event: ${result.isSuccess}")

        return result.isSuccess
    }
}
