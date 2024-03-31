package fe.linksheet.module.analytics.client

import android.os.Build
import androidx.lifecycle.LifecycleCoroutineScope
import fe.httpkt.Request
import fe.httpkt.ext.isHttpSuccess
import fe.httpkt.ext.readToString
import fe.httpkt.json.JsonBody
import fe.kotlin.extension.primitive.unixMillisAtZone
import fe.linksheet.BuildConfig
import fe.linksheet.module.analytics.*
import fe.linksheet.module.log.Logger
import fe.linksheet.module.network.NetworkStateService
import fe.linksheet.util.BuildType
import java.io.IOException
import java.time.format.DateTimeFormatter

class AptabaseAnalyticsClient(
    enabled: Boolean,
    coroutineScope: LifecycleCoroutineScope,
    identityData: TelemetryIdentityData,
    level: TelemetryLevel,
    networkState: NetworkStateService,
    logger: Logger,
    private val apiKey: String?,
) : AnalyticsClient(enabled, coroutineScope, identityData, level, networkState, logger = logger) {
    private val environmentInfo = EnvironmentInfo.from(identityData)

    companion object {
        const val SDK_VERSION = "aptabase-kotlin@0.0.8"

        val HOSTS = mapOf(
            "US" to "https://us.aptabase.com",
            "EU" to "https://eu.aptabase.com",
        )
    }

    data class EnvironmentInfo(
        var isDebug: Boolean,
        var osName: String,
        var osVersion: String,
        var locale: String,
        var appVersion: String,
        var appBuildNumber: String,
        var deviceModel: String,
        val sdkVersion: String,
    ) {
        companion object {
            fun from(data: TelemetryIdentityData): EnvironmentInfo {
                val manufacturer = data.data["manufacturer"]
                val model = data.data["model"]

                return EnvironmentInfo(
                    BuildType.current.allowDebug,
                    "Android",
                    Build.VERSION.RELEASE,
                    data.data.getOrDefault("locale", "<null>"),
                    (data.data["version_name"]!! + "-" + BuildConfig.FLAVOR + "-" + BuildType.current.name).lowercase(),
                    data.data["version_code"]!!,
                    if (manufacturer != null && model != null) "$manufacturer/$model" else "<null>",
                    SDK_VERSION
                )
            }
        }
    }

    private data class AptabaseEvent(
        val timestamp: String,
        val sessionId: String,
        val eventName: String,
        val systemProps: EnvironmentInfo,
        val props: Map<String, Any>,
    )

    private val request = Request {
        apiKey?.let { addHeaderImpl("App-Key", apiKey) }
    }

    private val baseUrl: String
    private val apiEvent: String
    private val apiEvents: String

    init {
        val parts = apiKey?.split("-").takeIf { it?.size == 3 }
        val region = parts?.get(1)
        baseUrl = HOSTS[region] ?: throw Exception("The Aptabase App Key $apiKey is invalid!")

        apiEvent = "$baseUrl/api/v0/event"
        apiEvents = "$baseUrl/api/v0/events"
    }

    private fun buildEvent(event: AnalyticsEvent): AptabaseEvent {
        val timestamp = event.unixMillis.unixMillisAtZone().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return AptabaseEvent(
            timestamp,
            identityData.data["sessionId"] ?: "<null>",
            event.name,
            environmentInfo,
            event.data
        )
    }

    @Throws(IOException::class)
    override fun send(event: AnalyticsEvent): Boolean {
        val aptabaseEvent = buildEvent(event)
        val con = request.post(apiEvent, body = JsonBody(aptabaseEvent))

        logger.info("Handled event ${event.name}: ${con.responseCode} (${con.readToString()})")
        return con.isHttpSuccess()
    }

    @Throws(IOException::class)
    override fun send(events: List<AnalyticsEvent>): Boolean {
        val aptabaseEvents = events.map { buildEvent(it) }
        val con = request.post(apiEvents, body = JsonBody(aptabaseEvents))

        logger.info("Handled events ${events.joinToString(separator = ",") { it.name }}: ${con.responseCode} (${con.readToString()})")
        return con.isHttpSuccess()
    }
}



