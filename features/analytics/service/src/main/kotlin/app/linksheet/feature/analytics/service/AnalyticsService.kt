@file:OptIn(ExperimentalAtomicApi::class)

package app.linksheet.feature.analytics.service

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import app.linksheet.api.SensitivePreference
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.analytics.BuildConfig
import app.linksheet.feature.analytics.preference.AnalyticsPreferences
import app.linksheet.mozilla.components.support.base.log.logger.Logger
import fe.android.lifecycle.koin.extension.applicationLifecycle
import fe.android.lifecycle.koin.extension.service
import fe.composekit.lifecycle.network.core.NetworkStateService
import fe.composekit.preference.asFunction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import org.koin.dsl.module
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(SensitivePreference::class)
val AnalyticsServiceModule = module {
    service<BaseAnalyticsService, AppPreferenceRepository, NetworkStateService> { _, preferences, networkState ->
        val analyticsPreferences = scope.get<AnalyticsPreferences>()
        AnalyticsService(
            analyticsSupported = BuildConfig.ANALYTICS_SUPPORTED,
            client = scope.get<AnalyticsClient>(),
            coroutineScope = applicationLifecycle.lifecycleCoroutineScope,
            level = preferences.asFunction(analyticsPreferences.telemetryLevel),
            showInfoDialog = preferences.asFunction(analyticsPreferences.telemetryShowInfoDialog),
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
    private val queueFlow = MutableStateFlow<BatchedEventQueue?>(null)

    private suspend fun awaitQueue() = queueFlow.filterNotNull().firstOrNull()

    override suspend fun changeLevel(newLevel: TelemetryLevel?) {
        awaitQueue()?.startWith(newLevel)
    }

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        logger.debug("onAppInitialized Received")
        val hasMadeChoice = !showInfoDialog()
        val initialLevel = if (hasMadeChoice) level() else null
        val eventQueue = BatchedEventQueue(client, coroutineScope, logger, initialLevel, networkState)
        queueFlow.emit(eventQueue)
    }

    override suspend fun onResume() {
        logger.debug("Resume received, starting processor")
        awaitQueue()?.start()
    }

    override suspend fun onStop() {
        awaitQueue()?.stop()
    }

    override suspend fun enqueue(event: AnalyticsEvent?): Boolean {
        logger.debug("enqueue")
        if (!analyticsSupported) return false
        val queue = awaitQueue()
        return queue?.enqueue(event) == true
    }
}
