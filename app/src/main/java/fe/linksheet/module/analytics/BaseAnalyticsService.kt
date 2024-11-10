package fe.linksheet.module.analytics

import fe.android.lifecycle.LifecycleAwareService

interface BaseAnalyticsService : LifecycleAwareService {
    fun changeLevel(newLevel: TelemetryLevel?)

    fun enqueue(event: AnalyticsEvent?): Boolean
}
