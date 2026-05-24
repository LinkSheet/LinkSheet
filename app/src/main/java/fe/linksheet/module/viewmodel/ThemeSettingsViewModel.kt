package fe.linksheet.module.viewmodel

import androidx.lifecycle.ViewModel
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.remoteconfig.preference.RemoteConfigRepository
import app.linksheet.feature.remoteconfig.preference.StoredRemotePreferences
import app.linksheet.feature.remoteconfig.util.LinkAssets
import fe.android.preference.helper.Preference
import fe.composekit.preference.ViewModelStatePreference
import fe.linksheet.composable.ui.ThemeV2
import fe.linksheet.module.preference.app.AppPreferences

class ThemeSettingsViewModel(
    val remoteConfigRepository: RemoteConfigRepository,
    val preferenceRepository: AppPreferenceRepository,
) : RootViewModel() {
    override val themeV2 = preferenceRepository.asViewModelState(AppPreferences.themeV2.themeV2)
    override val themeAmoled = preferenceRepository.asViewModelState(AppPreferences.themeV2.amoled)
    override val themeMaterialYou = preferenceRepository.asViewModelState(AppPreferences.themeV2.materialYou)
    override val linkAssets = remoteConfigRepository.asViewModelState(StoredRemotePreferences.linkAssets)
}

abstract class RootViewModel : ViewModel(){
    abstract val themeV2: ViewModelStatePreference<ThemeV2, ThemeV2, Preference.Mapped<ThemeV2, String>>
    abstract val themeAmoled: ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>
    abstract val themeMaterialYou: ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>
    abstract val linkAssets: ViewModelStatePreference<LinkAssets, LinkAssets, Preference.Mapped<LinkAssets, String>>
}
