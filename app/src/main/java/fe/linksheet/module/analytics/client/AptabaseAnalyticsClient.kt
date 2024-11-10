package fe.linksheet.module.analytics.client

import android.os.Build
import fe.httpkt.Request
import fe.httpkt.ext.isHttpSuccess
import fe.httpkt.ext.readToString
import fe.httpkt.json.JsonBody
import fe.linksheet.BuildConfig
import fe.linksheet.LinkSheetApp
import fe.linksheet.extension.koin.createLogger
import fe.linksheet.module.analytics.*
import fe.linksheet.module.log.Logger
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.util.BuildType
import fe.std.javatime.extension.unixMillisAtZone
import org.koin.dsl.module
import java.io.IOException
import java.time.format.DateTimeFormatter

@OptIn(SensitivePreference::class)
val aptabaseAnalyticsClientModule = module {
    single<AnalyticsClient> {
        val applicationContext = get<LinkSheetApp>()
        val preferences = get<AppPreferenceRepository>()
        val id = preferences.getOrPutInit(AppPreferences.telemetryId)
        val identity = preferences.get(AppPreferences.telemetryIdentity)

        AptabaseAnalyticsClient(
            identity.create(applicationContext, id),
            createLogger<AptabaseAnalyticsClient>(),
            BuildConfig.APTABASE_API_KEY
        )
    }
}

internal class AptabaseAnalyticsClient(
    private val identityData: TelemetryIdentityData,
    logger: Logger,
    private val apiKey: String?,
) : AnalyticsClient(logger = logger) {
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
    override fun sendEvents(events: List<AnalyticsEvent>): Boolean {
        val aptabaseEvents = events.map { buildEvent(it) }
        val con = request.post(apiEvents, body = JsonBody(aptabaseEvents))

        logger.info("Handled events ${events.joinToString(separator = ",") { it.name }}: ${con.responseCode} (${con.readToString()})")
        return con.isHttpSuccess()
    }
}



