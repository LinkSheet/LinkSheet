package fe.linksheet.usecase

import app.linksheet.api.BuildInfo
import app.linksheet.api.preference.AppStatePreferenceRepository
import fe.linksheet.module.preference.state.AppStatePreferences
import kotlinx.coroutines.flow.map

class ChangelogUseCase(
    private val stateRepository: AppStatePreferenceRepository,
    private val buildInfo: BuildInfo,
) {
    val version = buildInfo.versionName
    //    private val currentVersionDismissedPref = Preference.Boolean("cl_dis_${buildInfo.versionCode}", false)
    private val lastChangelogDismissed = stateRepository.asViewModelState(AppStatePreferences.lastClDismissed)
    private val lastChangelogDismissedFlow = lastChangelogDismissed.stateFlow
    val showChangelog = lastChangelogDismissedFlow.map { it != buildInfo.versionCode }

    fun dismiss() {
        lastChangelogDismissed.invoke(buildInfo.versionCode)
    }
}
