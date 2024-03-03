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
import fe.linksheet.module.analytics.AnalyticsEvent
import fe.linksheet.module.analytics.TelemetryIdentity
import fe.linksheet.module.log.impl.Logger
import fe.linksheet.module.network.NetworkState
import fe.linksheet.util.BuildType
import java.io.IOException
import java.time.format.DateTimeFormatter

class AptabaseAnalyticsClient(
    enabled: Boolean,
    coroutineScope: LifecycleCoroutineScope,
    private val environmentInfo: EnvironmentInfo,
    identity: TelemetryIdentity,
    networkState: NetworkState,
    logger: Logger,
    private val apiKey: String?,
) : AnalyticsClient(enabled, coroutineScope, identity, networkState, logger = logger) {

    companion object {
        const val SDK_VERSION = "aptabase-kotlin@0.0.8"

        val HOSTS = mapOf(
            "US" to "https://us.aptabase.com",
            "EU" to "https://eu.aptabase.com",
        )
    }

    private val request = Request {
        apiKey?.let { addHeaderImpl("App-Key", apiKey) }
    }

    private val baseUrl: String

    init {
        val parts = apiKey?.split("-").takeIf { it?.size == 3 }
        val region = parts?.get(1)
        baseUrl = HOSTS[region] ?: throw Exception("The Aptabase App Key $apiKey is invalid!")
    }

    private fun buildEvent(telemetryIdentity: TelemetryIdentity, event: AnalyticsEvent): AptabaseEvent {
        val timestamp = event.unixMillis.unixMillisAtZone().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val properties = telemetryIdentity.createEvent(event)
        return AptabaseEvent(timestamp, "TODO", event.name, environmentInfo, properties)
    }

    @Throws(IOException::class)
    override fun send(telemetryIdentity: TelemetryIdentity, event: AnalyticsEvent): Boolean {
        val aptabaseEvent = buildEvent(telemetryIdentity, event)
        val con = request.post("$baseUrl/api/v0/event", body = JsonBody(aptabaseEvent))
        logger.info("Handled event ${event.name}: ${con.responseCode} (${con.readToString()})")

        return con.isHttpSuccess()
    }

    @Throws(IOException::class)
    override fun send(telemetryIdentity: TelemetryIdentity, events: List<AnalyticsEvent>): Boolean {
        val aptabaseEvents = events.map { buildEvent(telemetryIdentity, it) }
        val con = request.post("$baseUrl/api/v0/events", body = JsonBody(aptabaseEvents))
        logger.info("Handled events ${events.joinToString(separator = ",") { it.name }}: ${con.responseCode} (${con.readToString()})")

        return con.isHttpSuccess()
    }
}

data class EnvironmentInfo(
    var isDebug: Boolean,
    var osName: String,
    var osVersion: String,
    var locale: String,
    var appVersion: String,
    var appBuildNumber: String,
    var deviceModel: String,
    val sdkVersion: String
) {
    companion object {
        fun create(context: Context): EnvironmentInfo {
            return EnvironmentInfo(
                BuildConfig.DEBUG,
                "Android",
                Build.VERSION.RELEASE ?: "",
                context.getCurrentLocale().language,
                if (BuildType.current == BuildType.Release) BuildConfig.VERSION_NAME
                else BuildConfig.COMMIT.substring(0, 6),
                BuildConfig.VERSION_CODE.toString(),
                Build.MODEL,
                AptabaseAnalyticsClient.SDK_VERSION
            )
        }
    }
}


private data class AptabaseEvent(
    val timestamp: String,
    val sessionId: String,
    val eventName: String,
    val systemProps: EnvironmentInfo,
    val props: Map<String, Any>
)
