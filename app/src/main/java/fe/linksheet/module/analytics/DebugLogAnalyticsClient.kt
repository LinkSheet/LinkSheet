package fe.linksheet.module.analytics

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import fe.linksheet.extension.koin.createLogger
import fe.linksheet.module.log.Logger
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

class DebugLogAnalyticsClient(
    coroutineScope: LifecycleCoroutineScope,
    identity: String = "Debug-Log",
    level: TelemetryLevel = TelemetryLevel.Basic,
    logger: Logger,
) : AnalyticsClient(true, coroutineScope, identity, level, logger = logger) {
    companion object {
        val debugLogAnalyticsModule = module {
            single<AnalyticsClient> {
                DebugLogAnalyticsClient(coroutineScope = get(), logger = createLogger<DebugLogAnalyticsClient>())
                    .init(get())
            }
        }
    }

    override fun setup(context: Context) {
        logger.info("Client set up")
    }

    override fun send(name: String, properties: Map<String, Any>): Boolean {
        logger.info("Tracking event $name with properties $properties")
        return true
    }
}
