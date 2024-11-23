package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.pm.CrossProfileApps
import android.os.Build
import android.os.UserHandle
import android.os.UserHandleHidden
import android.os.UserManager
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import dev.rikka.tools.refine.Refine
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
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

    val needSetupFlow = flowOfLazy { profileSwitcher.needsSetupAtLeastR() }

    fun isManagedProfile(): Boolean {
        return if (AndroidVersion.AT_LEAST_API_30_R) userManager.isManagedProfile
        else !userManager.isSystemUser
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getUserProfileInfo(): UserProfileInfo {
        val profiles = userManager.userProfiles.associateWith { Refine.unsafeCast<UserHandleHidden>(it) }
        val myUserId = UserHandleHidden.myUserId()

        var myUserHandle: UserHandleHidden? = null
        val otherHandles = mutableListOf<UserHandleHidden>()

        for ((_, hiddenUserHandle) in profiles) {
            if (hiddenUserHandle.identifier == myUserId) {
                myUserHandle = hiddenUserHandle
            } else {
                otherHandles.add(hiddenUserHandle)
            }
        }

        return UserProfileInfo(myUserHandle!!, otherHandles)
    }

    data class UserProfileInfo(
        val userHandle: UserHandleHidden,
        val otherHandles: MutableList<UserHandleHidden>,
    )

    var enabled = preferenceRepository.asState(AppPreferences.bottomSheetProfileSwitcher)
}

