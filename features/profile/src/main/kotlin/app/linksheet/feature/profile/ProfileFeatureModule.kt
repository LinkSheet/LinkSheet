package app.linksheet.feature.profile

import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.profile.core.AndroidProfileSwitcher
import app.linksheet.feature.profile.core.CrossProfileAppsCompat
import app.linksheet.feature.profile.preference.ProfilePreferences
import app.linksheet.feature.profile.service.ProfileService
import app.linksheet.feature.profile.viewmodel.ProfileSwitchingSettingsViewModel
import fe.android.lifecycle.koin.extension.service
import fe.composekit.preference.asFunction
import fe.droidkit.koin.getSystemServiceOrThrow
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ProfileFeatureModule = module {
    single {
        CrossProfileAppsCompat(context = get())
    }
    single {
        AndroidProfileSwitcher(
            appLabel = "LinkSheet",
            refineWrapper = get(),
//            appLabel = getResources().getString(R.string.app_name),
            crossProfileAppsCompat = get(),
            userManager = getSystemServiceOrThrow()
        )
    }
    service<ProfileService> {
        ProfileService(
            metaDataHandler = scope.get(),
            sendTarget = scope.get<AppPreferenceRepository>().asFunction(scope.get<ProfilePreferences>().sendTarget)
        )
    }
    viewModelOf(::ProfileSwitchingSettingsViewModel)
}
