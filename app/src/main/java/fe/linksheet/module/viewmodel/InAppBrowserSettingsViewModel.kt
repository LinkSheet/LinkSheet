package fe.linksheet.module.viewmodel


import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.getInstalledPackagesCompat
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import fe.kotlin.extension.iterable.filterIf
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.extension.android.isUserApp
import fe.linksheet.extension.android.launchIO
import fe.linksheet.extension.android.toImageBitmap
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.repository.DisableInAppBrowserInSelectedRepository
import fe.linksheet.module.viewmodel.base.BrowserCommonViewModel
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class InAppBrowserSettingsViewModel(
    context: Application,
    private val repository: DisableInAppBrowserInSelectedRepository,
    preferenceRepository: AppPreferenceRepository,
) : BrowserCommonViewModel(context, preferenceRepository) {
    val inAppBrowserMode = preferenceRepository.asViewModelState(AppPreferences.inAppBrowserSettings)

    private val _searchFilter = MutableStateFlow("")
    val searchFilter = _searchFilter.asStateFlow()

    private val installedPackages = flowOfLazy {
        context.packageManager.getInstalledPackagesCompat().filter { it.applicationInfo?.isUserApp() == true }.map {
            SelectableApp(
                applicationInfo = it.applicationInfo!!,
                label = it.applicationInfo!!.loadLabel(context.packageManager).toString()
            )
        }
    }

    private val disabledPackages = repository.getAll().map { list ->
        list.mapToSet { it.packageName }
    }

    private val items = installedPackages.combine(disabledPackages) { all, disabledPackages ->
        for (item in all) {
            item.update(item.packageName in disabledPackages)
        }

        all.sortedWith(SelectableApp.labelComparator)
    }

    val _filteredItems = items.combine(_searchFilter) { items, filter ->
        items.filterIf(filter = filter.isNotEmpty()) { item ->
            item.compareLabel.contains(filter, ignoreCase = true)
        } as List
    }

    fun search(query: String?) {
        _searchFilter.value = query ?: ""
    }

    fun save(app: SelectableApp, enabled: Boolean): Job {
        return launchIO {
            repository.insertOrDelete(enabled, app.packageName)
        }
    }
}

class SelectableApp(
    val applicationInfo: ApplicationInfo,
    val label: String,
    val selected: MutableState<Boolean> = mutableStateOf(false),
) {
    val compareLabel = label.lowercase()
    val packageName: String = applicationInfo.packageName

    companion object {
        val labelComparator = compareBy<SelectableApp> { it.compareLabel }
    }

    fun loadIcon(context: Context): ImageBitmap {
        return applicationInfo.loadIcon(context.packageManager).toImageBitmap()
    }

    fun update(newState: Boolean) {
        selected.value = newState
    }
}
