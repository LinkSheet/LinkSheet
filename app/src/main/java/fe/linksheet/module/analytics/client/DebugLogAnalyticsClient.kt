package fe.linksheet.module.analytics.client

import android.content.Context
import androidx.compose.ui.util.fastJoinToString
import fe.linksheet.extension.koin.createLogger
import fe.linksheet.module.analytics.AnalyticsClient
import fe.linksheet.module.analytics.AnalyticsEvent
import fe.linksheet.module.log.Logger
import org.koin.dsl.module

internal class DebugLogAnalyticsClient(
    logger: Logger,
) : AnalyticsClient(logger) {
    companion object {
        val module = module {
            single<AnalyticsClient> {
                DebugLogAnalyticsClient(logger = createLogger<DebugLogAnalyticsClient>())
            }
        }
    }

    override fun setup(context: Context) {
        logger.info("Client set up, using a NoOp-implementation, no events will be sent")
    }

    override fun sendEvents(events: List<AnalyticsEvent>): Boolean {
        logger.info("Tracking events ${events.fastJoinToString(separator = ",") { it.name }}")
        return true
    }
}
