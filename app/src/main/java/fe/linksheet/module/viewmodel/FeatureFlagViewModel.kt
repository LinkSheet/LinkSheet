package fe.linksheet.module.viewmodel

import android.app.Application
import fe.android.preference.helper.PreferenceRepository
import fe.linksheet.module.preference.AppPreferenceRepository

import fe.android.preference.helper.compose.getBooleanState
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.preference.FeatureFlagRepository
import fe.linksheet.module.preference.FeatureFlags
import fe.linksheet.module.viewmodel.base.BaseViewModel
import org.koin.core.qualifier.named

class FeatureFlagViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository,
    featureFlagRepository: FeatureFlagRepository
) : BaseViewModel(preferenceRepository) {

    val featureFlagShizuku = featureFlagRepository.getBooleanState(FeatureFlags.featureFlagShizuku)
}