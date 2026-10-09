package app.linksheet.feature.remoteconfig.usecase

import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.api.preference.AppStatePreferenceRepository
import app.linksheet.feature.remoteconfig.preference.RemoteConfigPreferences
import app.linksheet.feature.remoteconfig.preference.RemoteConfigStatePreferences

class RemoteConfigUseCase(
    repository: AppPreferenceRepository,
    stateRepository: AppStatePreferenceRepository,
    remoteConfigPreferences: RemoteConfigPreferences,
    remoteConfigStatePreferences: RemoteConfigStatePreferences,
) {
    val enabled = repository.asViewModelState(remoteConfigPreferences.enable)
    val dialogDismissed = stateRepository.asViewModelState(remoteConfigStatePreferences.dialogDismissed)

    fun update(enabled: Boolean) {
        dialogDismissed(true)
        enabled(enabled)
    }
}
