package fe.linksheet.module.viewmodel

import android.app.Application

import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.preference.FeatureFlagRepository
import fe.linksheet.module.preference.FeatureFlags
import fe.linksheet.module.viewmodel.base.BaseViewModel

class FeatureFlagViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository,
    featureFlagRepository: FeatureFlagRepository
) : BaseViewModel(preferenceRepository) {

    val featureFlagLinkSheetCompat = featureFlagRepository.getBooleanState(FeatureFlags.featureFlagLinkSheetCompat)
}
