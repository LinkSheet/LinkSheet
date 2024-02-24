package fe.linksheet.module.analytics

import android.content.Context
import fe.linksheet.extension.koin.createLogger
import fe.linksheet.module.log.Logger
import org.koin.dsl.module

class DebugLogAnalyticsClient(
    level: TelemetryLevel = TelemetryLevel.Basic,
    logger: Logger,
) : AnalyticsClient(true, "Identity-Log", level, logger) {
    companion object {
        val debugLogAnalyticsModule = module {
            single<AnalyticsClient> {
                DebugLogAnalyticsClient(TelemetryLevel.Basic, createLogger<DebugLogAnalyticsClient>()).init(get())
            }
        }
    }

    override fun setup(context: Context) {
        logger.info("Client set up")
    }

    override fun handle(name: String, properties: Map<String, Any>) {
        logger.info("Tracking event $name with properties $properties")
    }
}
