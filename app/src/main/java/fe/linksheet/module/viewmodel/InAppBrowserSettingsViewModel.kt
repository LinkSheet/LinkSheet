package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.AppPreferenceRepository


import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.extension.android.launchIO
import fe.linksheet.extension.android.queryAllResolveInfos
import fe.linksheet.extension.android.toDisplayActivityInfos
import fe.linksheet.module.preference.AppPreferences
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
    preferenceRepository: AppPreferenceRepository
) : BrowserCommonViewModel(context, preferenceRepository) {
    var inAppBrowserMode = preferenceRepository.asState(AppPreferences.inAppBrowserSettings)

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

    override fun save(selected: BrowserCommonSelected) = launchIO {
        selected.forEach { (activityInfo, enabled) ->
            repository.insertOrDelete(enabled, activityInfo.packageName)
        }
    }
}
