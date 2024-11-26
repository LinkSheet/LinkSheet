package fe.linksheet.module.viewmodel

import android.app.Application
import fe.android.preference.helper.compose.StatePreference
import fe.linksheet.R
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.flags.FeatureFlagRepository
import fe.linksheet.module.preference.flags.FeatureFlags
import fe.linksheet.module.viewmodel.base.BaseViewModel

class FeatureFlagViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository,
    featureFlagRepository: FeatureFlagRepository,
) : BaseViewModel(preferenceRepository) {
}

sealed class Flag(val pref: StatePreference<Boolean>) {
    class Full(pref: StatePreference<Boolean>, val headlineId: Int, val subtitleId: Int) : Flag(pref)
    class Simple(pref: StatePreference<Boolean>, val text: String) : Flag(pref)
}
