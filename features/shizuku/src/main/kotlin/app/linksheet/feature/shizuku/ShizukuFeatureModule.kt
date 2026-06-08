package app.linksheet.feature.shizuku

import android.content.Context
import app.linksheet.api.SystemInfoService
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.shizuku.preference.ShizukuPreferences
import app.linksheet.feature.shizuku.service.AndroidShizukuService
import app.linksheet.feature.shizuku.service.ShizukuFeatureService
import app.linksheet.feature.shizuku.service.ShizukuService
import app.linksheet.feature.shizuku.service.UserServiceConfig
import app.linksheet.feature.shizuku.viewmodel.ShizukuSettingsViewModel
import app.linksheet.util.buildconfig.StaticBuildInfo
import fe.android.lifecycle.koin.extension.service
import fe.composekit.preference.asFlow
import fe.droidkit.koin.getPackageManager
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ShizukuFeatureModule = module {
    single {
        val info = get<SystemInfoService>()
        AndroidShizukuService(
            eventBus = get(),
            packageManager = getPackageManager(),
            config = UserServiceConfig(
                packageName = get<Context>().packageName,
                versionCode = info.buildInfo.versionCode + if (StaticBuildInfo.IsDebug) 9999 else 0,
                debuggable = StaticBuildInfo.IsDebug,
                tag = info.getApplicationId()
            )
        )
    }
    service<ShizukuFeatureService> {
        val repo = scope.get<AppPreferenceRepository>()
        val preferences = scope.get<ShizukuPreferences>()
        val connection = scope.get<ShizukuService>()
        ShizukuFeatureService(
            eventBus = scope.get(),
            enabled = repo.asFlow(preferences.enable),
            autoDisableLinkHandlers = repo.asFlow(preferences.autoDisableLinkHandling),
            userServiceFlow = connection.userServiceFlow
        )
    }
    viewModelOf(::ShizukuSettingsViewModel)
}
