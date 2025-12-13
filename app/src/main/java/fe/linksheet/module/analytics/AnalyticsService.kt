package fe.linksheet.module.analytics

import androidx.lifecycle.LifecycleCoroutineScope
import fe.android.lifecycle.koin.extension.applicationLifecycle
import fe.android.lifecycle.koin.extension.service
import fe.composekit.lifecycle.network.core.NetworkStateService
import fe.linksheet.BuildConfig
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import mozilla.components.support.base.log.logger.Logger
import org.koin.dsl.module

@OptIn(SensitivePreference::class)
val AnalyticsServiceModule = module {
    service<BaseAnalyticsService, AppPreferenceRepository, NetworkStateService> { _, preferences, networkState ->
        val client = scope.get<AnalyticsClient>()

        val level = preferences.get(AppPreferences.telemetryLevel)
        val hasMadeChoice = !preferences.get(AppPreferences.telemetryShowInfoDialog)

        AnalyticsService(
            analyticsSupported = BuildConfig.ANALYTICS_SUPPORTED,
            client = client,
            coroutineScope = applicationLifecycle.lifecycleCoroutineScope,
            initialLevel = if (hasMadeChoice) level else null,
            networkState = networkState,
        )
    }
}

internal class AnalyticsService(
    private val analyticsSupported: Boolean,
    client: AnalyticsClient,
    coroutineScope: LifecycleCoroutineScope,
    initialLevel: TelemetryLevel? = null,
    networkState: NetworkStateService,
) : BaseAnalyticsService {
    private val logger = Logger("AnalyticsService")
    private val eventQueue = BatchedEventQueue(client, coroutineScope, logger, initialLevel, networkState)

    override fun changeLevel(newLevel: TelemetryLevel?) {
        eventQueue.startWith(newLevel)
    }

    override suspend fun onResume() {
        logger.debug("Resume received, starting processor")
        eventQueue.start()
    }

    override suspend fun onStop() {
        eventQueue.stop()
    }

    override fun enqueue(event: AnalyticsEvent?): Boolean {
        if (!analyticsSupported) return false
        return eventQueue.enqueue(event)
    }
}
