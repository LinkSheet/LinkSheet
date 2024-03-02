package fe.linksheet.module.analytics.client

import android.content.Context
import androidx.compose.ui.util.fastJoinToString
import androidx.lifecycle.LifecycleCoroutineScope
import fe.linksheet.extension.koin.single
import fe.linksheet.module.analytics.AnalyticsClient
import fe.linksheet.module.analytics.AnalyticsEvent
import fe.linksheet.module.analytics.TelemetryIdentity
import fe.linksheet.module.analytics.TelemetryLevel
import fe.linksheet.module.log.impl.Logger
import fe.linksheet.module.network.NetworkState
import org.koin.dsl.module

class DebugLogAnalyticsClient(
    coroutineScope: LifecycleCoroutineScope,
    identity: String = "Debug-Log",
    level: TelemetryLevel = TelemetryLevel.Basic,
    networkState: NetworkState,
    logger: Logger,
) : AnalyticsClient(true, coroutineScope, identity, level, networkState, logger = logger) {
    companion object {
        val module = module {
            single<AnalyticsClient, LifecycleCoroutineScope, NetworkState> { _, lifecycleCoroutineScope, networkState ->
                DebugLogAnalyticsClient(
                    coroutineScope = lifecycleCoroutineScope,
                    networkState = networkState,
                    logger = singletonLogger
                )
            }
        }
    }

    override fun setup(context: Context) {
        logger.info("Client set up")
    }

    override fun send(telemetryIdentity: TelemetryIdentity, event: AnalyticsEvent): Boolean {
        logger.info("Tracking event ${event.name}")
        return true
    }

    override fun send(telemetryIdentity: TelemetryIdentity, events: List<AnalyticsEvent>): Boolean {
        logger.info("Tracking events ${events.fastJoinToString(separator = ",") { it.name }}")
        return true
    }
}
