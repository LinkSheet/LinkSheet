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
import fe.linksheet.extension.startActivityWithConfirmation
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class BottomSheetSettingsViewModel(
    val context: Application,
    preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    private val appOpsManager = context.getSystemService<AppOpsManager>()!!

    var enableCopyButton = preferenceRepository.getBooleanState(Preferences.enableCopyButton)
    var hideAfterCopying = preferenceRepository.getBooleanState(Preferences.hideAfterCopying)
    var singleTap = preferenceRepository.getBooleanState(Preferences.singleTap)
    var enableSendButton = preferenceRepository.getBooleanState(Preferences.enableSendButton)
    var disableToasts = preferenceRepository.getBooleanState(Preferences.disableToasts)
    var gridLayout = preferenceRepository.getBooleanState(Preferences.gridLayout)
    var useTextShareCopyButtons =
        preferenceRepository.getBooleanState(Preferences.useTextShareCopyButtons)
    var dontShowFilteredItem =
        preferenceRepository.getBooleanState(Preferences.dontShowFilteredItem)
    var previewUrl = preferenceRepository.getBooleanState(Preferences.previewUrl)

    var usageStatsSorting = preferenceRepository.getBooleanState(Preferences.usageStatsSorting)
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