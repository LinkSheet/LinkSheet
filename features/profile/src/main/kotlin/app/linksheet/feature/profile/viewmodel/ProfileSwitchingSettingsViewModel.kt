package app.linksheet.feature.profile.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.app.core.MetaDataHandler
import app.linksheet.feature.profile.core.CrossProfile
import app.linksheet.feature.profile.core.ProfileStatus
import app.linksheet.feature.profile.core.ProfileSwitcher
import app.linksheet.feature.profile.preference.ProfilePreferences
import fe.composekit.extension.componentName
import fe.linksheet.util.ComponentEnabledStateFlags
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

class ProfileSwitchingSettingsViewModel internal constructor(
    private val profileSwitcher: ProfileSwitcher,
    private val metaDataHandler: MetaDataHandler,
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
    val sendTarget = preferenceRepository.asViewModelState(profilePreferences.sendTarget)

    fun checkIsManagedProfile(): Boolean {
        return profileSwitcher.checkIsManagedProfile()
    }

    fun launchCrossProfileInteractSettings(activity: Activity?) {
        if (activity == null) return
        profileSwitcher.launchCrossProfileInteractSettings(activity)
    }

    fun startOther(crossProfile: CrossProfile, activity: Activity?) {
        if (activity == null) return
        profileSwitcher.startOther(crossProfile, activity)
    }

    fun setForwardProfileActivities(state: Boolean) = viewModelScope.launch {
        val activities = metaDataHandler.getForwardProfileActivities()
        for (info in activities) {
            metaDataHandler.setComponentEnabled(
                info.componentName,
                if (state) ComponentEnabledStateFlags.COMPONENT_ENABLED_STATE_ENABLED else ComponentEnabledStateFlags.COMPONENT_ENABLED_STATE_DISABLED,
            )
        }
    }
}
