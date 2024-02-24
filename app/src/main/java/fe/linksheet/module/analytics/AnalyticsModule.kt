package fe.linksheet.module.analytics

import android.content.Context
import fe.android.preference.helper.compose.getState
import fe.linksheet.BuildConfig
import fe.linksheet.extension.koin.createLogger
import fe.linksheet.module.log.Logger
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.preference.AppPreferences
import org.koin.dsl.module
import kotlin.properties.Delegates

val analyticsModule = module {
    single<AnalyticsClient> {
        val preferenceRepository = get<AppPreferenceRepository>()
        val identity = preferenceRepository.getOrWriteInit(AppPreferences.telemetryIdentity)
        val level = preferenceRepository.getState(AppPreferences.telemetryLevel).value

        AptabaseAnalyticsClient(
            BuildConfig.ANALYTICS_SUPPORTED,
            identity,
            level,
            createLogger<AptabaseAnalyticsClient>(),
            BuildConfig.APTABASE_API_KEY
        ).init(get())
    }
}

abstract class AnalyticsClient(
    private val supported: Boolean,
    private val identity: String,
    private val level: TelemetryLevel,
    val logger: Logger
) {
    private lateinit var telemetryIdentity: TelemetryIdentity
    private var enabled by Delegates.notNull<Boolean>()

    protected open fun checkImplEnabled() = true

    protected abstract fun setup(context: Context)

    protected abstract fun handle(name: String, properties: Map<String, Any>)

    internal fun init(context: Context): AnalyticsClient {
        val implEnabled = supported && this.checkImplEnabled()
        enabled = implEnabled && level != TelemetryLevel.Disabled

        if (enabled) {
            telemetryIdentity = level.buildIdentity(context, identity)
            setup(context)
        }

        return this
    }

    fun track(event: AnalyticsEvent?): Boolean {
        if (enabled && event != null) {
            handle(event.name, telemetryIdentity.createEvent(event))
            return true
        }

        return false
    }

    fun updateLevel(newLevel: TelemetryLevel) {
        enabled = if (newLevel == TelemetryLevel.Disabled) false
        else supported && this.checkImplEnabled()
    }
}
