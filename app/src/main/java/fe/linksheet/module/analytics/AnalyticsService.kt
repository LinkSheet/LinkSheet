package fe.linksheet.module.analytics

import androidx.lifecycle.LifecycleCoroutineScope
import fe.android.lifecycle.LifecycleService
import fe.linksheet.BuildConfig
import fe.linksheet.extension.koin.service
import fe.linksheet.module.log.Logger
import fe.linksheet.module.network.NetworkStateService
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import org.koin.dsl.module

@OptIn(SensitivePreference::class)
val analyticsServiceModule = module {
    service<AnalyticsService, AppPreferenceRepository, NetworkStateService> { _, preferences, networkState ->
        val client = scope.get<AnalyticsClient>()

        val level = preferences.get(AppPreferences.telemetryLevel)
        val hasMadeChoice = !preferences.get(AppPreferences.telemetryShowInfoDialog)

        AnalyticsService(
            BuildConfig.ANALYTICS_SUPPORTED,
            client,
            applicationLifecycle.lifecycleCoroutineScope,
            if (hasMadeChoice) level else null,
            networkState,
            logger
        )
    }
}

class AnalyticsService(
    private val analyticsSupported: Boolean,
    client: AnalyticsClient,
    coroutineScope: LifecycleCoroutineScope,
    initialLevel: TelemetryLevel? = null,
    networkState: NetworkStateService,
    val logger: Logger,
) : LifecycleService {
    private val eventQueue = BatchedEventQueue(client, coroutineScope, logger, initialLevel, networkState)

    fun startWith(newLevel: TelemetryLevel?) {
        eventQueue.startWith(newLevel)
    }

    override suspend fun onResume() {
        logger.info("Resume received, starting processor")
        eventQueue.start()
    }

    override suspend fun onStop() {
        eventQueue.stop()
    }

    fun enqueue(event: AnalyticsEvent?): Boolean {
        if (!analyticsSupported) return false
        return eventQueue.enqueue(event)
    }
}
