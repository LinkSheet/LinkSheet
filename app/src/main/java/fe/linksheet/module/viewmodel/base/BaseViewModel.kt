package fe.linksheet.module.viewmodel.base

import androidx.lifecycle.ViewModel
import fe.android.preference.helper.BasePreference
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.RepositoryState
import fe.android.preference.helper.compose.getBooleanState
import fe.linksheet.module.preference.Preferences

abstract class BaseViewModel(preferenceRepository: PreferenceRepository) : ViewModel() {
    var alwaysShowPackageName = preferenceRepository.getBooleanState(
        Preferences.alwaysShowPackageName
    )

    fun <T, NT, P : BasePreference<T, NT>> updateState(
        state: RepositoryState<T, NT, P>,
        newState: NT
    ) = state.updateState(newState)
}