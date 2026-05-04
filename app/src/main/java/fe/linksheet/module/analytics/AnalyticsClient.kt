package fe.linksheet.module.analytics

import android.content.Context
import app.linksheet.mozilla.components.support.base.log.logger.Logger
import fe.android.lifecycle.LifecycleAwareService
import java.io.IOException

abstract class AnalyticsClient(val logger: Logger) : LifecycleAwareService {
    protected open fun checkImplEnabled() = true

    protected open fun setup(context: Context) {}

    @Throws(IOException::class)
    abstract fun sendEvents(events: List<AnalyticsEvent>): Boolean
}
