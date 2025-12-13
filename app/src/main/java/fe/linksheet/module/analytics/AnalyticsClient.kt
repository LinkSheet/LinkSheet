package fe.linksheet.module.analytics

import android.content.Context
import mozilla.components.support.base.log.logger.Logger
import java.io.IOException

abstract class AnalyticsClient(val logger: Logger) {
    protected open fun checkImplEnabled() = true

    protected open fun setup(context: Context) {}

    @Throws(IOException::class)
    abstract fun sendEvents(events: List<AnalyticsEvent>): Boolean
}
