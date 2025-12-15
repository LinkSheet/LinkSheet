package fe.linksheet.module.viewmodel


import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.linksheet.feature.profile.core.ProfileSwitcher
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

    val enableIgnoreLibRedirectButton = preferenceRepository.asViewModelState(AppPreferences.libRedirect.enableIgnoreLibRedirectButton)
    val hideAfterCopying = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.hideAfterCopying)
    val gridLayout = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.gridLayout)
    val dontShowFilteredItem = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.dontShowFilteredItem)
    val previewUrl = preferenceRepository.asViewModelState(AppPreferences.previewUrl)
    val enableRequestPrivateBrowsingButton = preferenceRepository.asViewModelState(AppPreferences.browser.enable)

    val usageStatsSorting = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.usageStatsSorting)
    val hideBottomSheetChoiceButtons = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.hideBottomSheetChoiceButtons)
    val hideReferringApp = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.hideReferringApp)

    val tapConfigSingle = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.tapConfig.single)
    val tapConfigDouble = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.tapConfig.double)
    val tapConfigLong = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.tapConfig.long)
    val expandOnAppSelect = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.expandOnAppSelect)
    val bottomSheetNativeLabel = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.bottomSheetNativeLabel)
    val bottomSheetProfileSwitcher = preferenceRepository.asViewModelState(AppPreferences.profileSwitcher.enable)

    val usageStatsPermission = UsageStatsPermission(context)

    var wasTogglingUsageStatsSorting by mutableStateOf(false)
}
