package fe.linksheet.module.statistic

import androidx.lifecycle.LifecycleOwner
import fe.android.lifecycle.LifecycleAwareService
import fe.android.lifecycle.koin.extension.service
import fe.linksheet.module.preference.PreferenceRepositoryModule
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.remoteconfig.RemoteConfigClientModule
import org.koin.dsl.module

val StatisticsModule = module {
    includes(PreferenceRepositoryModule)
    service<StatisticsService, AppPreferenceRepository> { _, preferences ->
        StatisticsService(preferences)
    }
}

class StatisticsService(val preferenceRepository: AppPreferenceRepository) : LifecycleAwareService {
    private val start = System.currentTimeMillis()

    @OptIn(SensitivePreference::class)
    override suspend fun onStop() {
        val currentUseTime = preferenceRepository.get(AppPreferences.useTimeMs)
        val usedFor = System.currentTimeMillis() - start

        preferenceRepository.edit {
            put(AppPreferences.useTimeMs, currentUseTime + usedFor)
        }
    }

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
    }
}


