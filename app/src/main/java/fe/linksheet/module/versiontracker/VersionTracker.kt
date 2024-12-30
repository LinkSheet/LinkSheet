package fe.linksheet.module.versiontracker

import androidx.lifecycle.LifecycleOwner
import com.google.gson.FormattingStyle
import com.google.gson.Gson
import fe.android.lifecycle.LifecycleAwareService
import fe.gson.globalGsonModule
import fe.linksheet.BuildConfig
import fe.linksheet.extension.koin.service
import fe.linksheet.module.analytics.AnalyticsEvent
import fe.linksheet.module.analytics.AppStart
import fe.linksheet.module.analytics.BaseAnalyticsService
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.preferenceRepositoryModule
import fe.linksheet.util.buildconfig.BuildType
import fe.linksheet.util.buildconfig.LinkSheetInfo
import org.koin.core.module.Module
import org.koin.dsl.module

val VersionTrackerModule = VersionTrackerModule()

private fun VersionTrackerModule(): Module = module {
    includes(globalGsonModule, preferenceRepositoryModule)

    service<VersionTracker, BaseAnalyticsService, AppPreferenceRepository> { _, analyticsService, preferences ->
        val gson = scope.get<Gson>().newBuilder().setFormattingStyle(FormattingStyle.COMPACT).create()

        VersionTracker(analyticsService, preferences, gson)
    }
}

private class VersionTracker(
    private val analyticsService: BaseAnalyticsService,
    val preferenceRepository: AppPreferenceRepository,
    val gson: Gson,
) : LifecycleAwareService {
    private val lastVersionsService by lazy {
        LastVersionService(gson, LinkSheetInfo.buildInfo)
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

        if (BuildType.current.allowDebug) {
            // TODO: Remove once user is given the choice to opt in/out
            analyticsService.enqueue(createAppStartEvent(lastVersion))
        }

        val lastVersions = preferenceRepository.get(AppPreferences.lastVersions)
        val lastVersionJson = lastVersionsService.handleVersions(lastVersions)

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
