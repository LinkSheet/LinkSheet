package fe.linksheet.module.viewmodel

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.UserHandleHidden
import android.os.UserManager
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import dev.rikka.tools.refine.Refine
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.profile.CrossProfile
import fe.linksheet.module.profile.ProfileSwitcher
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.flowOfLazy

class ProfileSwitchingSettingsViewModel(
    val context: Application,
    val profileSwitcher: ProfileSwitcher,
    preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {
    private val userManager = context.getSystemService<UserManager>()!!

    val needSetup = flowOfLazy { profileSwitcher.needsSetupAtLeastR() }

    fun isManagedProfile(): Boolean {
        return if (AndroidVersion.AT_LEAST_API_30_R) userManager.isManagedProfile
        else !userManager.isSystemUser
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getUserProfileInfo(): UserProfileInfo {
        val crossProfiles = profileSwitcher.getProfilesInternal() ?: emptyList()

        val profiles = userManager.userProfiles.associateWith { Refine.unsafeCast<UserHandleHidden>(it) }
        val myUserId = UserHandleHidden.myUserId()

        var myUserHandle: UserHandleHidden? = null
        val otherHandles = mutableListOf<Pair<UserHandleHidden, CrossProfile?>>()

        for ((_, hiddenUserHandle) in profiles) {
            if (hiddenUserHandle.identifier == myUserId) {
                myUserHandle = hiddenUserHandle
            } else {
                val crossProfile = crossProfiles.firstOrNull { it.id == hiddenUserHandle.identifier }
                otherHandles.add(hiddenUserHandle to crossProfile)
            }
        }

        return UserProfileInfo(myUserHandle!!, otherHandles)
    }

    data class UserProfileInfo(
        val userHandle: UserHandleHidden,
        val otherHandles: MutableList<Pair<UserHandleHidden, CrossProfile?>>,
    )

    var enabled = preferenceRepository.asState(AppPreferences.bottomSheetProfileSwitcher)

    fun launchCrossProfileInteractSettings(activity: Activity) {
        profileSwitcher.launchCrossProfileInteractSettings(activity)
    }
}

