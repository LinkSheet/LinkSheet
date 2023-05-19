package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.tasomaniac.openwith.resolver.BrowserHandler
import com.tasomaniac.openwith.resolver.BrowserResolver
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import com.tasomaniac.openwith.resolver.DisplayActivityInfo.Companion.sortByValueAndName
import fe.linksheet.data.dao.base.PackageEntityDao
import fe.linksheet.extension.ioAsync
import fe.linksheet.extension.ioLaunch
import fe.linksheet.extension.mapToSet
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.repository.PreferredBrowserRepository
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class PreferredBrowserViewModel(
    val context: Application,
    private val repository: PreferredBrowserRepository,
    preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    var browserMode = preferenceRepository.getState(Preferences.browserMode)
    var selectedBrowser = preferenceRepository.getStringState(Preferences.selectedBrowser)

    private val whitelistedBrowsersPackages = repository.getAll().map { list ->
        list.mapToSet { it.packageName }
    }

    val browsers = flowOfLazy {
        BrowserResolver.queryDisplayActivityInfoBrowsers(true)
    }

    val whitelistedBrowsers =
        browsers.combine(whitelistedBrowsersPackages) { browsers, whitelistedBrowsersPackages ->
            browsers.map {
                it to (it.packageName in whitelistedBrowsersPackages)
            }.sortByValueAndName().toMap()
        }

    fun saveWhitelistedBrowsers(activityInfoState: WhitelistedBrowsers) = ioLaunch {
        activityInfoState.forEach { (activityInfo, enabled) ->
            repository.insertOrDelete(enabled, activityInfo.packageName)
        }
    }

    fun updateBrowserMode(mode: BrowserHandler.BrowserMode) {
        if (this.browserMode.value == BrowserHandler.BrowserMode.SelectedBrowser && this.browserMode.value != mode && this.selectedBrowser.value != null) {
            ioLaunch { repository.deleteByPackageName(selectedBrowser.value!!) }
        }

        this.browserMode.updateState(mode)
    }
}

typealias WhitelistedBrowsers = MutableMap<DisplayActivityInfo, Boolean>
