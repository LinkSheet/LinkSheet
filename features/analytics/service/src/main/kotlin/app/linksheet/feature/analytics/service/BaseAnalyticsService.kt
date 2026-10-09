package app.linksheet.feature.analytics.service

import fe.android.lifecycle.LifecycleAwareService

interface BaseAnalyticsService : LifecycleAwareService {
    suspend fun changeLevel(newLevel: TelemetryLevel?)

    suspend fun enqueue(event: AnalyticsEvent?): Boolean
}
