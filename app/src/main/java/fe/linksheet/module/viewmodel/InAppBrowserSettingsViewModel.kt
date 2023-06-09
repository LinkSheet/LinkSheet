package fe.linksheet.module.viewmodel

import android.app.Application
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getState
import fe.linksheet.extension.android.ioLaunch
import fe.linksheet.extension.mapToSet
import fe.linksheet.extension.android.queryAllResolveInfos
import fe.linksheet.extension.android.toDisplayActivityInfos
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.repository.DisableInAppBrowserInSelectedRepository
import fe.linksheet.module.viewmodel.base.BrowserCommonSelected
import fe.linksheet.module.viewmodel.base.BrowserCommonViewModel
import fe.linksheet.resolver.DisplayActivityInfo.Companion.sortByValueAndName
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class InAppBrowserSettingsViewModel(
    context: Application,
    private val repository: DisableInAppBrowserInSelectedRepository,
    preferenceRepository: PreferenceRepository
) : BrowserCommonViewModel(context, preferenceRepository) {
    var inAppBrowserMode = preferenceRepository.getState(Preferences.inAppBrowserSettings)

    private val disableInAppBrowserInSelectedPackages = repository.getAll().map { list ->
        list.mapToSet { it.packageName }
    }

    private val packages = flowOfLazy {
        context.packageManager.queryAllResolveInfos(true).toDisplayActivityInfos(context, true)
    }

    override fun items() =
        packages.combine(disableInAppBrowserInSelectedPackages) { packages, disableInAppBrowserInSelectedPackages ->
            packages.map {
                it to (it.packageName in disableInAppBrowserInSelectedPackages)
            }.sortByValueAndName().toMap()
        }

    override fun save(selected: BrowserCommonSelected) = ioLaunch {
        selected.forEach { (activityInfo, enabled) ->
            repository.insertOrDelete(enabled, activityInfo.packageName)
        }
    }
}