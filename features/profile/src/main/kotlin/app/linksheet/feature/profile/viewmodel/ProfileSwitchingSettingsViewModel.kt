package app.linksheet.feature.profile.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.profile.core.CrossProfile
import app.linksheet.feature.profile.core.ProfileStatus
import app.linksheet.feature.profile.core.ProfileSwitcher
import app.linksheet.feature.profile.preference.ProfilePreferences
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapNotNull

class ProfileSwitchingSettingsViewModel internal constructor(
    private val profileSwitcher: ProfileSwitcher,
    preferenceRepository: AppPreferenceRepository,
    profilePreferences: ProfilePreferences,
) : ViewModel() {
    val status = flowOfLazy { profileSwitcher.getStatus() }
    val userProfileInfo = status
        .filterIsInstance<ProfileStatus.Available>()
        .mapNotNull {
            profileSwitcher.getUserProfileInfo(it)
        }

    val enabled = preferenceRepository.asViewModelState(profilePreferences.enable)

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
