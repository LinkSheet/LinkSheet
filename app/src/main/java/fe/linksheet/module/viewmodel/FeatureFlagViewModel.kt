package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import fe.android.preference.helper.compose.StatePreference

class FeatureFlagViewModel(
    val context: Application,
) : ViewModel() {
}

sealed class Flag(val pref: StatePreference<Boolean>) {
    class Full(pref: StatePreference<Boolean>, val headlineId: Int, val subtitleId: Int) : Flag(pref)
    class Simple(pref: StatePreference<Boolean>, val text: String) : Flag(pref)
}
