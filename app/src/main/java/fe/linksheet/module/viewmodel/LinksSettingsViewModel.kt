package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import app.linksheet.feature.clearurls.core.ClearUrlsProviderLoader
import fe.composekit.preference.asFlow
import fe.composekit.preference.util.reload
import fe.std.result.getOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import app.linksheet.api.preference.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel

class LinksSettingsViewModel(
    val context: Application,
    private val preferenceRepository: AppPreferenceRepository,
    private val clearUrlsProviderLoader: ClearUrlsProviderLoader,
) : BaseViewModel(preferenceRepository) {
    val useClearUrls = preferenceRepository.asViewModelState(AppPreferences.useClearUrls)
    val useFastForwardRules = preferenceRepository.asViewModelState(AppPreferences.useFastForwardRules)
    val enableLibRedirect = preferenceRepository.asViewModelState(AppPreferences.libRedirect.enable)
    val followRedirects = preferenceRepository.asViewModelState(AppPreferences.followRedirects.enable)
    val enableDownloader = preferenceRepository.asViewModelState(AppPreferences.downloader.enable)
    val enableAmp2Html = preferenceRepository.asViewModelState(AppPreferences.amp2Html.enable)
    val urlPreview = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.openGraphPreview.enable)
    val resolveEmbeds = preferenceRepository.asViewModelState(AppPreferences.resolveEmbeds)
    val selectLinkParameters = preferenceRepository.asViewModelState(AppPreferences.bottomSheet.selectLinkParameters)
    val openWithoutTrackingButton =
        preferenceRepository.asViewModelState(AppPreferences.bottomSheet.openWithoutTrackingButton)

    private val providerDialog = MutableStateFlow(ClearUrlsProviderDialogState())
    val clearUrlsProviders = combine(
        providerDialog,
        preferenceRepository.asFlow(AppPreferences.clearUrlsRemoveAllQueryProviders)
    ) { dialog, selected ->
        dialog.copy(
            providers = dialog.providers.filter { it.contains(dialog.search, ignoreCase = true) },
            selected = selected
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ClearUrlsProviderDialogState())

    init {
        viewModelScope.launch {
            val providers = withContext(Dispatchers.IO) {
                clearUrlsProviderLoader.getProviders().getOrNull()
                    ?.map { it.key }?.filter { it != "globalRules" }?.sortedBy { it.lowercase() }
            }
            providerDialog.update { it.copy(providers = providers.orEmpty(), loading = false, failed = providers == null) }
        }
    }

    fun showClearUrlsProviders(show: Boolean) {
        providerDialog.update { it.copy(visible = show, search = "") }
    }

    fun searchClearUrlsProviders(search: String) {
        providerDialog.update { it.copy(search = search) }
    }

    fun setRemoveAllQueryParameters(provider: String, enabled: Boolean) {
        if (provider !in providerDialog.value.providers) return
        val selected = preferenceRepository.get(AppPreferences.clearUrlsRemoveAllQueryProviders)
        preferenceRepository.put(AppPreferences.clearUrlsRemoveAllQueryProviders, if (enabled) selected + provider else selected - provider)
        // Direct writes do not notify the cached flow shared by the UI and resolver.
        preferenceRepository.reload(AppPreferences.clearUrlsRemoveAllQueryProviders.key)
    }
}

data class ClearUrlsProviderDialogState(
    val visible: Boolean = false,
    val loading: Boolean = true,
    val failed: Boolean = false,
    val search: String = "",
    val providers: List<String> = emptyList(),
    val selected: Set<String> = emptySet(),
)
