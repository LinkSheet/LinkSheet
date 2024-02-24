package fe.linksheet.module.analytics

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import fe.linksheet.BuildConfig
import fe.linksheet.extension.koin.createLogger
import fe.linksheet.module.analytics.client.AptabaseAnalyticsClient
import fe.linksheet.module.log.Logger
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.preference.AppPreferences
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.time.delay
import org.koin.dsl.module
import java.time.Duration
import kotlin.properties.Delegates

val analyticsModule = module {
    single<AnalyticsClient> {
        val preferenceRepository = get<AppPreferenceRepository>()
        val identity = preferenceRepository.getOrWriteInit(AppPreferences.telemetryIdentity)
        val level = preferenceRepository.getState(AppPreferences.telemetryLevel).value

        AptabaseAnalyticsClient(
            BuildConfig.ANALYTICS_SUPPORTED,
            get(),
            identity,
            level,
            createLogger<AptabaseAnalyticsClient>(),
            BuildConfig.APTABASE_API_KEY
        ).init(get())
    }
}

abstract class AnalyticsClient(
    private val supported: Boolean,
    private val coroutineScope: LifecycleCoroutineScope,
    protected val identity: String,
    private val initialLevel: TelemetryLevel,
    private val timeout: Duration = Duration.ofSeconds(10),
    val logger: Logger
) {
    private lateinit var currentLevel: TelemetryLevel
    private var eventSender: Job? = null
    private var enabled by Delegates.notNull<Boolean>()

    private val eventQueue = Channel<AnalyticsEvent>(capacity = UNLIMITED)

    protected open fun checkImplEnabled() = true

    protected open fun setup(context: Context) {}

    protected abstract fun send(name: String, properties: Map<String, Any>): Boolean

    @OptIn(DelicateCoroutinesApi::class)
    internal fun init(context: Context, level: TelemetryLevel = initialLevel): AnalyticsClient {
        val implEnabled = supported && this.checkImplEnabled()
        enabled = implEnabled && level != TelemetryLevel.Disabled
        currentLevel = level

        if (enabled) {
            val telemetryIdentity = initialLevel.buildIdentity(context, identity)
            setup(context)

            // TODO: Can events be batched?
            eventSender = coroutineScope.launch(Dispatchers.IO) {
                while (!eventQueue.isClosedForReceive) {
                    val event = eventQueue.receive()
                    logger.debug("Sending event ${event.name}..")

                    send(event.name, telemetryIdentity.createEvent(event))
                    delay(timeout)
                }
            }
        } else {
            eventSender?.cancel()
        }

        return this
    }

    fun updateLevel(context: Context, newLevel: TelemetryLevel) {
        init(context, newLevel)
    }

    // TODO: Handle remaining events in channel when app is stopped/destroyed
    fun enqueue(event: AnalyticsEvent?) {
        // Always enqueue, event if not enabled, so events can be sent if user decides to allow analytics
        if (event != null) {
            coroutineScope.launch { eventQueue.trySend(event) }
        }
    }

    fun shutdown() {
        eventSender?.cancel()
    }
}
