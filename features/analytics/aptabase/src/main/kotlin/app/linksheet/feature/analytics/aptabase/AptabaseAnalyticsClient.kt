package app.linksheet.feature.analytics.aptabase

import androidx.lifecycle.LifecycleOwner
import app.linksheet.api.BuildInfo
import app.linksheet.api.DeviceInfo
import app.linksheet.api.SensitivePreference
import app.linksheet.api.SystemInfoService
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.analytics.preference.AnalyticsPreferences
import app.linksheet.feature.analytics.service.AnalyticsClient
import app.linksheet.feature.analytics.service.AnalyticsEvent
import app.linksheet.feature.analytics.service.TelemetryIdentityData
import app.linksheet.lib.http.TaggedRequest
import app.linksheet.mozilla.components.support.base.log.logger.Logger
import fe.android.lifecycle.koin.extension.service
import fe.httpkt.ext.isHttpSuccess
import fe.httpkt.ext.readToString
import fe.httpkt.json.JsonBody
import fe.std.javatime.extension.unixMillisAtZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.koin.dsl.module
import java.io.IOException
import java.time.format.DateTimeFormatter

@OptIn(SensitivePreference::class)
val aptabaseAnalyticsClientModule = module {
    service<AnalyticsClient> {
        val analyticsPreferences = scope.get<AnalyticsPreferences>()
        val preferences = scope.get<AppPreferenceRepository>()
        val systemInfo = scope.get<SystemInfoService>()

        AptabaseAnalyticsClient(
            fnIdentityData = {
                withContext(Dispatchers.IO) {
                    val identity = preferences.get(analyticsPreferences.telemetryIdentity)
                    val id = preferences.getOrPutInit(analyticsPreferences.telemetryId)
                    identity.create(applicationContext, id)
                }
            },
            apiKey = BuildConfig.APTABASE_API_KEY,
            buildInfo = systemInfo.buildInfo,
            deviceInfo = systemInfo.deviceInfo
        )
    }
}

internal class AptabaseAnalyticsClient(
    private val fnIdentityData: suspend () -> TelemetryIdentityData,
    logger: Logger = Logger("AptabaseAnalyticsClient"),
    private val apiKey: String?,
    private val buildInfo: BuildInfo,
    private val deviceInfo: DeviceInfo
) : AnalyticsClient(logger = logger) {


    companion object {
        val HOSTS = mapOf(
            "US" to "https://us.aptabase.com",
            "EU" to "https://eu.aptabase.com",
        )
    }

    private val request = TaggedRequest {
        apiKey?.let { addHeader("App-Key", apiKey) }
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

    private var identityDataFlow = MutableStateFlow<Pair<TelemetryIdentityData, EnvironmentInfo>?>(null)

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        val identityData = fnIdentityData()
        val environmentInfo = EnvironmentInfo.from(buildInfo, deviceInfo, identityData)
        identityDataFlow.emit(identityData to environmentInfo)
    }

    private fun buildEvent(
        data: Pair<TelemetryIdentityData, EnvironmentInfo>,
        event: AnalyticsEvent
    ): AptabaseEvent {
        val (identityData, environmentInfo) = data
        val timestamp = event.unixMillis
            .unixMillisAtZone()
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return AptabaseEvent(
            timestamp = timestamp,
            sessionId = identityData.data["sessionId"] ?: "<null>",
            eventName = event.name,
            systemProps = environmentInfo,
            props = event.data
        )
    }

    @Throws(IOException::class)
    override suspend fun sendEvents(events: List<AnalyticsEvent>): Boolean {
        val data = identityDataFlow.filterNotNull().firstOrNull() ?: return false
        val aptabaseEvents = events.map { buildEvent(data, it) }
        val con = request.post(apiEvents, body = JsonBody(aptabaseEvents))

        logger.info("Handled events ${events.joinToString(separator = ",") { it.name }}: ${con.responseCode} (${con.readToString()})")
        return con.isHttpSuccess()
    }
}
