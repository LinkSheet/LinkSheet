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

    val status = flowOfLazy { profileSwitcher.getStatus() }

    val enabled = preferenceRepository.asState(AppPreferences.bottomSheetProfileSwitcher)

    fun launchCrossProfileInteractSettings(activity: Activity) {
        profileSwitcher.launchCrossProfileInteractSettings(activity)
    }

    fun startOther(crossProfile: CrossProfile, activity: Activity) {
        profileSwitcher.startOther(crossProfile, activity)
    }
}

