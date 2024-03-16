package fe.linksheet.module.statistic

import androidx.lifecycle.Lifecycle
import fe.linksheet.BuildConfig
import fe.linksheet.extension.koin.service
import fe.linksheet.module.lifecycle.Service
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.SensitivePreference
import org.koin.dsl.module

val statisticsModule = module {
    service<StatisticsService, AppPreferenceRepository> { _, preferences ->
        StatisticsService(preferences)
    }
}

class StatisticsService(val preferenceRepository: AppPreferenceRepository) : Service {
    private val start = System.currentTimeMillis()

    @OptIn(SensitivePreference::class)
    override fun stop(lifecycle: Lifecycle) {
        val currentUseTime = preferenceRepository.get(AppPreferences.useTimeMs)
        val usedFor = System.currentTimeMillis() - start

        preferenceRepository.edit {
            put(AppPreferences.useTimeMs, currentUseTime + usedFor)
            put(AppPreferences.lastVersion, BuildConfig.VERSION_CODE)
        }
    }
}
