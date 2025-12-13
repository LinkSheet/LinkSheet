package fe.linksheet.module.analytics.client

import android.content.Context
import fe.linksheet.module.analytics.AnalyticsClient
import fe.linksheet.module.analytics.AnalyticsEvent
import mozilla.components.support.base.log.logger.Logger
import org.koin.dsl.module

internal class DebugLogAnalyticsClient(
    logger: Logger,
) : AnalyticsClient(logger) {
    companion object {
        val module = module {
            single<AnalyticsClient> {
                DebugLogAnalyticsClient(logger = Logger("DebugLogAnalyticsClient"))
            }
        }
    }

    override fun setup(context: Context) {
    }

    override fun sendEvents(events: List<AnalyticsEvent>): Boolean {
        return true
    }
}
