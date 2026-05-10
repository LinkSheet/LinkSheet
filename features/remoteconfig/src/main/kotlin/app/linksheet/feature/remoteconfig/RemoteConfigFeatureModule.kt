package app.linksheet.feature.remoteconfig

import androidx.work.WorkManager
import app.linksheet.api.SystemInfoService
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.remoteconfig.preference.RemoteConfigPreferences
import app.linksheet.feature.remoteconfig.preference.RemoteConfigRepository
import app.linksheet.feature.remoteconfig.service.AndroidRemoteConfigClient
import app.linksheet.feature.remoteconfig.service.RemoteConfigService
import app.linksheet.feature.remoteconfig.worker.RemoteAssetFetcherWorker
import fe.android.lifecycle.koin.extension.service
import fe.composekit.preference.asFlow
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val RemoteConfigFeatureModule = module {
    singleOf(::RemoteConfigRepository)
    single {
        AndroidRemoteConfigClient(
            appName = "LinkSheet",
//            appName = getResources().getString(string.app_name),
            buildInfo = get<SystemInfoService>().buildInfo,
            client = get(),
        )
    }
    service<RemoteConfigService> {
        val remoteConfig = scope.get<RemoteConfigPreferences>()
        RemoteConfigService(
            workManager = WorkManager.getInstance(applicationContext),
            enabled = scope.get<AppPreferenceRepository>().asFlow(remoteConfig.enable)
        )
    }
    workerOf(::RemoteAssetFetcherWorker)
}
