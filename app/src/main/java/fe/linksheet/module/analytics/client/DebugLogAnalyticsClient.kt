package fe.linksheet.module.analytics.client

import android.content.Context
import androidx.compose.ui.util.fastJoinToString
import androidx.lifecycle.LifecycleCoroutineScope
import fe.linksheet.extension.koin.createLogger
import fe.linksheet.extension.koin.single
import fe.linksheet.module.analytics.*
import fe.linksheet.module.log.Logger
import fe.linksheet.module.network.NetworkStateService
import org.koin.dsl.module

class DebugLogAnalyticsClient(
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
        logger.info("Client set up")
    }

    override fun sendEvents(events: List<AnalyticsEvent>): Boolean {
        logger.info("Tracking events ${events.fastJoinToString(separator = ",") { it.name }}")
        return true
    }
}
