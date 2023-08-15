package fe.linksheet.module.viewmodel

import android.app.Application
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getBooleanState
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.viewmodel.base.BaseViewModel
import org.koin.core.qualifier.named

class FeatureFlagViewModel(
    val context: Application,
    featureFlagRepository: PreferenceRepository
) : BaseViewModel(featureFlagRepository) {
    companion object{
        val featureFlagName = "feature_flags"
        val featureFlagNamed = named(featureFlagName)
    }

    val featureFlagShizuku = featureFlagRepository.getBooleanState(Preferences.featureFlagShizuku)
}