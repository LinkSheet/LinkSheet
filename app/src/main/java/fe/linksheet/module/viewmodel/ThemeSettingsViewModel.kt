package fe.linksheet.module.viewmodel

import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.remoteconfig.RemoteConfigPreferences
import fe.linksheet.module.remoteconfig.RemoteConfigRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel

class ThemeSettingsViewModel(
    val remoteConfigRepository: RemoteConfigRepository,
    val preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {
    val themeV2 = preferenceRepository.asViewModelState(AppPreferences.themeV2)
    val themeAmoled = preferenceRepository.asViewModelState(AppPreferences.themeAmoled)
    val themeMaterialYou = preferenceRepository.asViewModelState(AppPreferences.themeMaterialYou)
    val linkAssets = remoteConfigRepository.asViewModelState(RemoteConfigPreferences.linkAssets)
}
