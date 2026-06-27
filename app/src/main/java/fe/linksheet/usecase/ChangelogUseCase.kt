package fe.linksheet.usecase

import app.linksheet.api.BuildInfo
import app.linksheet.api.preference.AppStatePreferenceRepository
import fe.composekit.preference.asFlow
import fe.linksheet.module.preference.state.AppStatePreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ChangelogUseCase(
    private val stateRepository: AppStatePreferenceRepository,
    private val buildInfo: BuildInfo,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val version = buildInfo.versionName
    //    private val currentVersionDismissedPref = Preference.Boolean("cl_dis_${buildInfo.versionCode}", false)
    private val lastChangelogDismissedFlow = stateRepository.asFlow(AppStatePreferences.lastClDismissed)
    val showChangelog = lastChangelogDismissedFlow.map { it != buildInfo.versionCode }

    suspend fun dismiss() = withContext(ioDispatcher) {
        stateRepository.put(AppStatePreferences.lastClDismissed, buildInfo.versionCode)
    }
}
