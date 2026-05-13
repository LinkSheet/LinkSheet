package app.linksheet.feature.shizuku

import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.shizuku.preference.ShizukuPreferences
import app.linksheet.feature.shizuku.service.AndroidShizukuService
import app.linksheet.feature.shizuku.service.ShizukuFeatureService
import app.linksheet.feature.shizuku.viewmodel.ShizukuSettingsViewModel
import fe.android.lifecycle.koin.extension.service
import fe.composekit.preference.asFlow
import fe.droidkit.koin.getPackageManager
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ShizukuFeatureModule = module {
    single {
        AndroidShizukuService(
            eventBus = get(),
            packageManager = getPackageManager()
        )
    }
    service<ShizukuFeatureService> {
        val repo = scope.get<AppPreferenceRepository>()
        val preferences = scope.get<ShizukuPreferences>()
        ShizukuFeatureService(
            eventBus = scope.get(),
            enabled = repo.asFlow(preferences.enable),
            autoDisableLinkHandlers = repo.asFlow(preferences.autoDisableLinkHandling)
        )
    }
    viewModelOf(::ShizukuSettingsViewModel)
}
