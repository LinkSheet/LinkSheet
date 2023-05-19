package fe.linksheet.module.viewmodel.base

import androidx.lifecycle.ViewModel
import fe.linksheet.module.preference.BasePreference
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.preference.RepositoryState

abstract class BaseViewModel(preferenceRepository: PreferenceRepository) : ViewModel() {
    var alwaysShowPackageName = preferenceRepository.getBooleanState(Preferences.alwaysShowPackageName)

    fun <T, NT, P : BasePreference<T, NT>> updateState(
        state: RepositoryState<T, NT, P>,
        newState: NT
    ) {
        state.updateState(newState)
    }
}