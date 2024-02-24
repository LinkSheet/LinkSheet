package fe.linksheet.module.analytics.client

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import fe.linksheet.extension.koin.createLogger
import fe.linksheet.module.analytics.AnalyticsClient
import fe.linksheet.module.analytics.TelemetryLevel
import fe.linksheet.module.log.Logger
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
