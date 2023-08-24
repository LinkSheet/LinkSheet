package fe.linksheet.module.viewmodel.base

import androidx.lifecycle.ViewModel
import fe.android.preference.helper.BasePreference
import fe.linksheet.module.preference.AppPreferenceRepository

import fe.android.preference.helper.compose.RepositoryState
import fe.android.preference.helper.compose.getBooleanState
import fe.linksheet.module.preference.AppPreferences

abstract class BaseViewModel(preferenceRepository: AppPreferenceRepository) : ViewModel() {
    var alwaysShowPackageName = preferenceRepository.getBooleanState(
        AppPreferences.alwaysShowPackageName
    )

    fun <T : Any, NT, P : BasePreference<T, NT>> updateState(
        state: RepositoryState<T, NT, P>,
        newState: NT
    ) = state.updateState(newState)
}