package fe.linksheet.module.versiontracker

import androidx.lifecycle.LifecycleOwner
import app.linksheet.api.SystemInfoService
import app.linksheet.feature.analytics.service.AnalyticsEvent
import app.linksheet.feature.analytics.service.AppStart
import app.linksheet.feature.analytics.service.BaseAnalyticsService
import app.linksheet.util.buildconfig.StaticBuildInfo
import com.google.gson.Gson
import fe.android.lifecycle.LifecycleAwareService
import fe.android.lifecycle.koin.extension.service
import fe.gson.GlobalGsonModule
import fe.gson.GsonQualifier
import fe.linksheet.BuildConfig
import fe.linksheet.module.preference.PreferenceRepositoryModule
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.systeminfo.SystemInfoServiceModule
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

val VersionTrackerModule = module {
    includes(GlobalGsonModule, SystemInfoServiceModule, PreferenceRepositoryModule)

    service<VersionTracker, BaseAnalyticsService, AppPreferenceRepository> { _, analyticsService, preferences ->
        VersionTracker(
            analyticsService = analyticsService,
            preferenceRepository = preferences,
            systemInfoService = scope.get<SystemInfoService>(),
            gson = scope.get(qualifier(GsonQualifier.Compact))
        )
    }
}

internal class VersionTracker(
    private val analyticsService: BaseAnalyticsService,
    val preferenceRepository: AppPreferenceRepository,
    private val systemInfoService: SystemInfoService,
    val gson: Gson,
) : LifecycleAwareService {
    private val lastVersionsService by lazy {
        LastVersionService(gson, systemInfoService.buildInfo)
    }

    private fun createAppStartEvent(lastVersion: Int): AnalyticsEvent {
        return when {
            lastVersion == -1 -> AppStart.FirstRun
            BuildConfig.VERSION_CODE > lastVersion -> AppStart.Updated(lastVersion)
            else -> AppStart.Default
        }
    }

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        val lastVersion = preferenceRepository.get(AppPreferences.lastVersion)

        if (StaticBuildInfo.IsDebug) {
            // TODO: Remove once user is given the choice to opt in/out
            analyticsService.enqueue(createAppStartEvent(lastVersion))
        }

        val lastVersions = preferenceRepository.get(AppPreferences.lastVersions)
        val lastVersionJson = lastVersionsService.handleVersions(lastVersions, true)

        preferenceRepository.edit {
            lastVersionJson?.let {
                put(AppPreferences.lastVersions, it)
            }

            put(AppPreferences.lastVersion, BuildConfig.VERSION_CODE)
        }
    }

    override suspend fun onStop() {
    }
}
