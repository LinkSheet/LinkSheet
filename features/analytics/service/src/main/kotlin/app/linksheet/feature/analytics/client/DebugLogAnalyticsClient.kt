package app.linksheet.feature.analytics.client

import android.content.Context
import app.linksheet.feature.analytics.service.AnalyticsClient
import app.linksheet.feature.analytics.service.AnalyticsEvent
import app.linksheet.mozilla.components.support.base.log.logger.Logger
import org.koin.dsl.module

class DebugLogAnalyticsClient(
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

    override suspend fun sendEvents(events: List<AnalyticsEvent>): Boolean {
        return true
    }
}
