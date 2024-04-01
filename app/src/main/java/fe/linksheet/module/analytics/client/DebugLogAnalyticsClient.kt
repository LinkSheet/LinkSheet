package fe.linksheet.module.analytics.client

import android.content.Context
import androidx.compose.ui.util.fastJoinToString
import androidx.lifecycle.LifecycleCoroutineScope
import fe.linksheet.extension.koin.single
import fe.linksheet.module.analytics.*
import fe.linksheet.module.log.Logger
import fe.linksheet.module.network.NetworkStateService
import org.koin.dsl.module

class DebugLogAnalyticsClient(
    coroutineScope: LifecycleCoroutineScope,
    identityData: TelemetryIdentityData,
    level: TelemetryLevel,
    networkState: NetworkStateService,
    logger: Logger,
) : AnalyticsClient(true, coroutineScope, identityData, level, networkState, logger = logger) {
    companion object {
        val module = module {
            single<AnalyticsClient, NetworkStateService> { _, networkState ->
                DebugLogAnalyticsClient(
                    coroutineScope = applicationLifecycle.coroutineScope,
                    networkState = networkState,
                    identityData = TelemetryIdentity.Full.create(applicationContext, "Debug"),
                    level = TelemetryLevel.Exhaustive,
                    logger = logger
                )
            }
        }
    }

    override fun setup(context: Context) {
        logger.info("Client set up")
    }

    override fun send(events: List<AnalyticsEvent>): Boolean {
        logger.info("Tracking events ${events.fastJoinToString(separator = ",") { it.name }}")
        return true
    }
}
