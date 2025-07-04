package fe.linksheet.module.viewmodel

import android.app.Activity
import android.app.Application
import fe.linksheet.feature.profile.CrossProfile
import fe.linksheet.feature.profile.ProfileStatus
import fe.linksheet.feature.profile.ProfileSwitcher
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapNotNull

class ProfileSwitchingSettingsViewModel(
    val context: Application,
    private val profileSwitcher: ProfileSwitcher,
    preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {
    val status = flowOfLazy { profileSwitcher.getStatus() }
    val userProfileInfo = status
        .filterIsInstance<ProfileStatus.Available>()
        .mapNotNull {
            profileSwitcher.getUserProfileInfo(it)
        }

    val enabled = preferenceRepository.asViewModelState(AppPreferences.bottomSheetProfileSwitcher)

    fun checkIsManagedProfile(): Boolean {
        return profileSwitcher.checkIsManagedProfile()
    }

    fun launchCrossProfileInteractSettings(activity: Activity?) {
        if (activity == null) return
        profileSwitcher.launchCrossProfileInteractSettings(activity)
    }

    fun startOther(crossProfile: CrossProfile, activity: Activity?) {
        if(activity == null) return
        profileSwitcher.startOther(crossProfile, activity)
    }
}

