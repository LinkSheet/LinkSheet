package fe.linksheet.module.viewmodel

import android.app.Activity
import android.app.AppOpsManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
import fe.linksheet.module.preference.AppPreferenceRepository

import fe.android.preference.helper.compose.getBooleanState
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class BottomSheetSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    private val appOpsManager = context.getSystemService<AppOpsManager>()!!

    val enableIgnoreLibRedirectButton =
        preferenceRepository.getBooleanState(AppPreferences.enableIgnoreLibRedirectButton)
    var enableCopyButton = preferenceRepository.getBooleanState(AppPreferences.enableCopyButton)
    var hideAfterCopying = preferenceRepository.getBooleanState(AppPreferences.hideAfterCopying)
    var singleTap = preferenceRepository.getBooleanState(AppPreferences.singleTap)
    var enableSendButton = preferenceRepository.getBooleanState(AppPreferences.enableSendButton)
    var gridLayout = preferenceRepository.getBooleanState(AppPreferences.gridLayout)
    var useTextShareCopyButtons =
        preferenceRepository.getBooleanState(AppPreferences.useTextShareCopyButtons)
    var dontShowFilteredItem =
        preferenceRepository.getBooleanState(AppPreferences.dontShowFilteredItem)
    var previewUrl = preferenceRepository.getBooleanState(AppPreferences.previewUrl)
    var enableRequestPrivateBrowsingButton = preferenceRepository.getBooleanState(
        AppPreferences.enableRequestPrivateBrowsingButton
    )

    var usageStatsSorting = preferenceRepository.getBooleanState(AppPreferences.usageStatsSorting)

    var wasTogglingUsageStatsSorting by mutableStateOf(false)

    fun openUsageStatsSettings(activity: Activity) {
        activity.startActivityWithConfirmation(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        wasTogglingUsageStatsSorting = true
    }

    fun getUsageStatsAllowed(context: Context): Boolean {
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )

        return mode == AppOpsManager.MODE_ALLOWED
    }
}