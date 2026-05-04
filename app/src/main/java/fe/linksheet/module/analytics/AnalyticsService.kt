package fe.linksheet.module.analytics

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import app.linksheet.mozilla.components.support.base.log.logger.Logger
import fe.android.lifecycle.koin.extension.applicationLifecycle
import fe.android.lifecycle.koin.extension.service
import fe.composekit.lifecycle.network.core.NetworkStateService
import fe.composekit.preference.asFunction
import fe.linksheet.BuildConfig
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import org.koin.dsl.module

@OptIn(SensitivePreference::class)
val AnalyticsServiceModule = module {
    service<BaseAnalyticsService, AppPreferenceRepository, NetworkStateService> { _, preferences, networkState ->
        AnalyticsService(
            analyticsSupported = BuildConfig.ANALYTICS_SUPPORTED,
            client = scope.get<AnalyticsClient>(),
            coroutineScope = applicationLifecycle.lifecycleCoroutineScope,
            level = preferences.asFunction(AppPreferences.telemetryLevel),
            showInfoDialog = preferences.asFunction(AppPreferences.telemetryShowInfoDialog),
            networkState = networkState,
        )
    }
}

internal class AnalyticsService(
    private val analyticsSupported: Boolean,
    private val client: AnalyticsClient,
    private val coroutineScope: LifecycleCoroutineScope,
    private val level: () -> TelemetryLevel,
    private val showInfoDialog: () -> Boolean,
    private val networkState: NetworkStateService,
) : BaseAnalyticsService {
    private val logger = Logger("AnalyticsService")
    private lateinit var eventQueue: BatchedEventQueue

    override fun changeLevel(newLevel: TelemetryLevel?) {
        eventQueue.startWith(newLevel)
    }

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        val hasMadeChoice = !showInfoDialog()
        val initialLevel = if (hasMadeChoice) level() else null
        eventQueue = BatchedEventQueue(client, coroutineScope, logger, initialLevel, networkState)
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
