package app.linksheet.module.viewmodel.privacy


import android.app.Application
import androidx.lifecycle.viewModelScope
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.engine.database.repository.CacheRepository
import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.resolver.Amp2HtmlRepository
import fe.linksheet.module.repository.resolver.ResolvedRedirectRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrivacyIncognitoSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository,
    private val amp2HtmlRepository: Amp2HtmlRepository,
    private val resolvedRedirectRepository: ResolvedRedirectRepository,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val cacheRepository: CacheRepository,
    private val logPersistService: LogPersistService,
    private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel(preferenceRepository) {
    val enable = preferenceRepository.asViewModelState(AppPreferences.privacy.incognito.enable)
    val disableLogs = preferenceRepository.asViewModelState(AppPreferences.privacy.incognito.disableLogs)
    val disableLogPersistence = preferenceRepository.asViewModelState(AppPreferences.privacy.incognito.disableLogsPersistence)

    fun clearAll() = viewModelScope.launch {
        amp2HtmlRepository.eraseAll()
        resolvedRedirectRepository.eraseAll()
        appSelectionHistoryRepository.eraseAll()
        preferredAppRepository.deleteAllNotAlwaysPreferred()
        cacheRepository.eraseAll()

        withContext(ioDispatcher) {
            logPersistService.getLogSessions().count { logPersistService.delete(it.id) }
        }
    }
}
