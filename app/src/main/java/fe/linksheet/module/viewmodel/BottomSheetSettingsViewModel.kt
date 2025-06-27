package fe.linksheet.module.viewmodel


import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fe.linksheet.feature.profile.ProfileSwitcher
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.permission.UsageStatsPermission
import fe.linksheet.module.viewmodel.base.BaseViewModel

class BottomSheetSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository,
    experimentsRepository: ExperimentRepository,
    val profileSwitcher: ProfileSwitcher,
) : BaseViewModel(preferenceRepository) {

    val enableIgnoreLibRedirectButton = preferenceRepository.asViewModelState(AppPreferences.enableIgnoreLibRedirectButton)
    val hideAfterCopying = preferenceRepository.asViewModelState(AppPreferences.hideAfterCopying)
    val gridLayout = preferenceRepository.asViewModelState(AppPreferences.gridLayout)
    val dontShowFilteredItem = preferenceRepository.asViewModelState(AppPreferences.dontShowFilteredItem)
    val previewUrl = preferenceRepository.asViewModelState(AppPreferences.previewUrl)
    val enableRequestPrivateBrowsingButton =
        preferenceRepository.asViewModelState(AppPreferences.enableRequestPrivateBrowsingButton)

    val usageStatsSorting = preferenceRepository.asViewModelState(AppPreferences.usageStatsSorting)
    val hideBottomSheetChoiceButtons = preferenceRepository.asViewModelState(AppPreferences.hideBottomSheetChoiceButtons)

    val tapConfigSingle = preferenceRepository.asViewModelState(AppPreferences.tapConfigSingle)
    val tapConfigDouble = preferenceRepository.asViewModelState(AppPreferences.tapConfigDouble)
    val tapConfigLong = preferenceRepository.asViewModelState(AppPreferences.tapConfigLong)
    val expandOnAppSelect = preferenceRepository.asViewModelState(AppPreferences.expandOnAppSelect)
    val bottomSheetNativeLabel = preferenceRepository.asViewModelState(AppPreferences.bottomSheetNativeLabel)
    val bottomSheetProfileSwitcher = preferenceRepository.asViewModelState(AppPreferences.bottomSheetProfileSwitcher)

    val usageStatsPermission = UsageStatsPermission(context)

    var wasTogglingUsageStatsSorting by mutableStateOf(false)
}
