package fe.linksheet.module.analytics

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import fe.linksheet.BuildConfig
import fe.linksheet.extension.koin.service
import fe.linksheet.module.analytics.client.AptabaseAnalyticsClient
import fe.linksheet.module.analytics.client.EnvironmentInfo
import fe.linksheet.module.lifecycle.Service
import fe.linksheet.module.log.Logger
import fe.linksheet.module.network.NetworkStateService
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.SensitivePreference
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
        val identity = preferences.getOrPutInit(AppPreferences.telemetryIdentity)
        val level by preferences.asState(AppPreferences.telemetryLevel)

        AptabaseAnalyticsClient(
            BuildConfig.ANALYTICS_SUPPORTED,
            applicationLifecycle.coroutineScope,
            EnvironmentInfo.create(applicationContext),
            level.buildIdentity(applicationContext, identity),
            networkState,
            logger,
            BuildConfig.APTABASE_API_KEY
        )
    }
}

abstract class AnalyticsClient(
    private val supported: Boolean,
    private val coroutineScope: LifecycleCoroutineScope,
    protected val identity: TelemetryIdentity,
    private val networkState: NetworkStateService,
    val logger: Logger
) : Service {

    private lateinit var currentLevel: TelemetryLevel
    private var eventSender: Job? = null
    private var enabled by Delegates.notNull<Boolean>()

    private val eventQueue = Channel<AnalyticsEvent>(capacity = UNLIMITED)

    companion object {
        const val BATCH_EVENTS = 5
        const val BATCHING_TIMEOUT_MILLIS = 15 * 1000L
        const val SEND_TRIES = 5
        val TRY_DELAY: (Int) -> Long = { attemptNo -> 10 * 1000L * 2.0.pow(attemptNo).toLong() }
    }

    protected open fun checkImplEnabled() = true

    protected open fun setup(context: Context) {}

    @Throws(IOException::class)
    protected abstract fun send(telemetryIdentity: TelemetryIdentity, event: AnalyticsEvent): Boolean

    @Throws(IOException::class)
    protected abstract fun send(telemetryIdentity: TelemetryIdentity, events: List<AnalyticsEvent>): Boolean

    override fun start(lifecycle: Lifecycle) {
//        val implEnabled = supported && this.checkImplEnabled()
//        enabled = implEnabled && level != TelemetryLevel.Disabled
//        currentLevel = level
//
//        if (enabled) {
//            val telemetryIdentity = initialLevel.buildIdentity(context, identity)
//            setup(context)
//
//
//        } else {
//            eventSender?.cancel()
//        }

        eventSender = coroutineScope.launch(Dispatchers.IO) {
            sendEvents(identity)
        }

//        return this
    }

    override fun stop(lifecycle: Lifecycle) {
        eventSender?.cancel()
    }


    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun sendEvents(telemetryIdentity: TelemetryIdentity) {
        var lastSend = -1L

        while (!eventQueue.isClosedForReceive) {
            val batchedEvents = LinkedList<AnalyticsEvent>()

            val firstEvent = eventQueue.receive()
            batchedEvents.add(firstEvent)

            val timeout = withTimeoutOrNull(BATCHING_TIMEOUT_MILLIS) {
                repeat(BATCH_EVENTS - 1) {
                    val additionalEvent = eventQueue.receive()
                    batchedEvents.add(additionalEvent)
                }
            }

            val timeoutExceeded = timeout == null
            if (!timeoutExceeded && lastSend != -1L) {
                val diff = System.currentTimeMillis() - lastSend
                val waitMillis = BATCHING_TIMEOUT_MILLIS - diff
                if (waitMillis > 0) {
                    delay(waitMillis)
                }
            }

            logger.debug("Sending events (${batchedEvents.size}, timeout: $timeoutExceeded)")
            trySend(telemetryIdentity, batchedEvents)

            lastSend = System.currentTimeMillis()
        }
    }

    private suspend fun trySend(telemetryIdentity: TelemetryIdentity, events: List<AnalyticsEvent>): Boolean {
        logger.debug("Awaiting internet access..")
        networkState.awaitNetworkConnection()
        logger.debug("Internet connection available")

        for (i in 0 until SEND_TRIES) {
            logger.debug("Trying to send events (attemptNo: ${i + 1})")
            runCatching {
                val success = send(telemetryIdentity, events)
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

//    fun updateLevel(context: Context, newLevel: TelemetryLevel) {
//        start(context, newLevel)
//    }

    // TODO: Handle remaining events in channel when app is stopped/destroyed
    fun enqueue(event: AnalyticsEvent?) {
        // Always enqueue, event if not enabled, so events can be sent if user decides to allow analytics
        if (event != null) {
            coroutineScope.launch { eventQueue.trySend(event) }
        }
    }
}
