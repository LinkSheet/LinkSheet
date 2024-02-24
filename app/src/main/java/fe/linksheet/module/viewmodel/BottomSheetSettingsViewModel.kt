package fe.linksheet.module.viewmodel

import android.app.AppOpsManager
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
import fe.linksheet.module.preference.AppPreferenceRepository


import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.preference.permission.UsageStatsPermission
import fe.linksheet.module.viewmodel.base.BaseViewModel

class BottomSheetSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {

    val enableIgnoreLibRedirectButton =
        preferenceRepository.getBooleanState(AppPreferences.enableIgnoreLibRedirectButton)
    var hideAfterCopying = preferenceRepository.getBooleanState(AppPreferences.hideAfterCopying)
    var gridLayout = preferenceRepository.getBooleanState(AppPreferences.gridLayout)
    var dontShowFilteredItem =
        preferenceRepository.getBooleanState(AppPreferences.dontShowFilteredItem)
    var previewUrl = preferenceRepository.getBooleanState(AppPreferences.previewUrl)
    var enableRequestPrivateBrowsingButton = preferenceRepository.getBooleanState(
        AppPreferences.enableRequestPrivateBrowsingButton
    )

    var usageStatsSorting = preferenceRepository.getBooleanState(AppPreferences.usageStatsSorting)
    val hideBottomSheetChoiceButtons = preferenceRepository.getBooleanState(AppPreferences.hideBottomSheetChoiceButtons)

    val usageStatsPermission = UsageStatsPermission(context)

    var wasTogglingUsageStatsSorting by mutableStateOf(false)
}
