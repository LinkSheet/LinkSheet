package fe.linksheet.module.statistic

import fe.android.lifecycle.LifecycleService
import fe.linksheet.BuildConfig
import fe.linksheet.extension.koin.service
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import org.koin.dsl.module

val statisticsModule = module {
    service<StatisticsService, AppPreferenceRepository> { _, preferences ->
        StatisticsService(preferences)
    }
}

class StatisticsService(val preferenceRepository: AppPreferenceRepository) : LifecycleService {
    private val start = System.currentTimeMillis()

    @OptIn(SensitivePreference::class)
    override suspend fun onStop() {
        val currentUseTime = preferenceRepository.get(AppPreferences.useTimeMs)
        val usedFor = System.currentTimeMillis() - start

        preferenceRepository.edit {
            put(AppPreferences.useTimeMs, currentUseTime + usedFor)
            put(AppPreferences.lastVersion, BuildConfig.VERSION_CODE)
        }
    }
}
