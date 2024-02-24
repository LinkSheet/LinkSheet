package fe.linksheet.module.analytics.client

import android.content.Context
import android.os.Build
import androidx.lifecycle.LifecycleCoroutineScope
import fe.httpkt.Request
import fe.httpkt.ext.isHttpSuccess
import fe.httpkt.ext.readToString
import fe.httpkt.json.JsonBody
import fe.kotlin.extension.primitive.unixMillisAtZone
import fe.linksheet.BuildConfig
import fe.linksheet.extension.android.getCurrentLocale
import fe.linksheet.module.analytics.AnalyticsClient
import fe.linksheet.module.analytics.TelemetryLevel
import fe.linksheet.module.log.impl.Logger
import fe.linksheet.util.BuildType
import java.time.format.DateTimeFormatter

class AptabaseAnalyticsClient(
    enabled: Boolean,
    coroutineScope: LifecycleCoroutineScope,
    identity: String,
    level: TelemetryLevel,
    logger: Logger,
    private val apiKey: String?,
) : AnalyticsClient(enabled, coroutineScope, identity, level, logger = logger) {
    companion object {
        private const val SDK_VERSION = "aptabase-kotlin@0.0.8"

        val HOSTS = mapOf(
            "US" to "https://us.aptabase.com",
            "EU" to "https://eu.aptabase.com",
        )
    }

    private val request = Request {
        apiKey?.let { addHeaderImpl("App-Key", apiKey) }
    }

    private val apiUrl: String
    private lateinit var environmentInfo: EnvironmentInfo

    init {
        val parts = apiKey?.split("-").takeIf { it?.size == 3 }
        val region = parts?.get(1)
        val baseUrl = HOSTS[region] ?: throw Exception("The Aptabase App Key $apiKey is invalid!")

        apiUrl = "$baseUrl/api/v0/event"
    }

    override fun setup(context: Context) {
        environmentInfo = EnvironmentInfo(
            BuildConfig.DEBUG,
            "Android",
            Build.VERSION.RELEASE ?: "",
            context.getCurrentLocale().language,
            if (BuildType.current == BuildType.Release) BuildConfig.VERSION_NAME
            else BuildConfig.COMMIT.substring(0, 6),
            BuildConfig.VERSION_CODE.toString(),
            Build.MODEL,
            SDK_VERSION
        )
    }

    override fun send(name: String, properties: Map<String, Any>): Boolean {
        val now = System.currentTimeMillis()
        val timestamp = now.unixMillisAtZone().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        val event = AptabaseEvent(timestamp, identity, name, environmentInfo, properties)
        val con = request.post(apiUrl, body = JsonBody(event))
        logger.info("Handled event $name: ${con.responseCode} (${con.readToString()})")

        return con.isHttpSuccess()
    }
}

private data class EnvironmentInfo(
    var isDebug: Boolean,
    var osName: String,
    var osVersion: String,
    var locale: String,
    var appVersion: String,
    var appBuildNumber: String,
    var deviceModel: String,
    val sdkVersion: String
)


private data class AptabaseEvent(
    val timestamp: String,
    val sessionId: String,
    val eventName: String,
    val systemProps: EnvironmentInfo,
    val props: Map<String, Any>
)
