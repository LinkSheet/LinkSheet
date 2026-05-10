package fe.linksheet.module.viewmodel

import app.linksheet.feature.remoteconfig.preference.RemoteConfigRepository
import app.linksheet.feature.remoteconfig.preference.StoredRemotePreferences
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class ThemeSettingsViewModel(
    val remoteConfigRepository: RemoteConfigRepository,
    val preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {
    val themeV2 = preferenceRepository.asViewModelState(AppPreferences.themeV2.themeV2)
    val themeAmoled = preferenceRepository.asViewModelState(AppPreferences.themeV2.amoled)
    val themeMaterialYou = preferenceRepository.asViewModelState(AppPreferences.themeV2.materialYou)
    val linkAssets = remoteConfigRepository.asViewModelState(StoredRemotePreferences.linkAssets)
}
