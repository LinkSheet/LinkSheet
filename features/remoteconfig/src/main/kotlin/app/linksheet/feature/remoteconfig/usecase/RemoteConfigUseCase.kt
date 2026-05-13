package app.linksheet.feature.remoteconfig.usecase

import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.api.preference.AppStatePreferenceRepository
import app.linksheet.feature.remoteconfig.preference.RemoteConfigPreferences
import app.linksheet.feature.remoteconfig.preference.RemoteConfigStatePreferences

class RemoteConfigUseCase(
    private val repository: AppPreferenceRepository,
    private val stateRepository: AppStatePreferenceRepository,
    private val remoteConfigPreferences: RemoteConfigPreferences,
    private val remoteConfigStatePreferences: RemoteConfigStatePreferences,
) {
    val enabled = repository.asViewModelState(remoteConfigPreferences.enable)
    val dialogDismissed = stateRepository.asViewModelState(remoteConfigStatePreferences.dialogDismissed)

    fun update(enabled: Boolean) {
        dialogDismissed(true)
        enabled(enabled)
    }
}
