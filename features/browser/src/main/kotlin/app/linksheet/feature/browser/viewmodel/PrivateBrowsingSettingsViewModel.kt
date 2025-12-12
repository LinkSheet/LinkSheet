package app.linksheet.feature.browser.viewmodel

import androidx.lifecycle.ViewModel
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.browser.database.repository.PrivateBrowsingBrowserRepository
import app.linksheet.feature.browser.preference.BrowserPreferences
import kotlinx.coroutines.flow.map

class PrivateBrowsingSettingsViewModel internal constructor(
    private val repository: PrivateBrowsingBrowserRepository,
    preferenceRepository: AppPreferenceRepository,
    browserPreferences: BrowserPreferences,
) : ViewModel() {

    val enabled = preferenceRepository.asViewModelState(browserPreferences.enable)

    val all = repository.getAll().map { list ->
        list.mapTo(LinkedHashSet()) { it.flatComponentName }
    }
}
