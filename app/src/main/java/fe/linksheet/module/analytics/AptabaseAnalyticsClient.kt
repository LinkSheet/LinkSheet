package fe.linksheet.module.analytics

import android.content.Context
import com.aptabase.Aptabase
import fe.linksheet.module.log.Logger

class AptabaseAnalyticsClient(
    enabled: Boolean,
    identity: String,
    level: TelemetryLevel,
    logger: Logger,
    private val apiKey: String?,
) : AnalyticsClient(enabled, identity, level, logger) {
    override fun checkImplEnabled() = apiKey != null

    override fun setup(context: Context) {
        Aptabase.instance.initialize(context, apiKey!!)
    }

    override fun handle(name: String, properties: Map<String, Any>) {
        Aptabase.instance.trackEvent(name, properties)
    }
}
