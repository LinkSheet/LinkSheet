package fe.linksheet.module.viewmodel

import android.app.Application
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import com.tasomaniac.openwith.resolver.DisplayActivityInfo.Companion.sortByValueAndName
import fe.linksheet.extension.ioLaunch
import fe.linksheet.extension.mapToSet
import fe.linksheet.extension.queryAllResolveInfos
import fe.linksheet.extension.toDisplayActivityInfos
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.repository.DisableInAppBrowserInSelectedRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class InAppBrowserSettingsViewModel(
    val context: Application,
    private val repository: DisableInAppBrowserInSelectedRepository,
    preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    private val disableInAppBrowserInSelectedPackages = repository.getAll().map { list ->
        list.mapToSet { it.packageName }
    }

    private val packages = flowOfLazy {
        context.packageManager.queryAllResolveInfos(true).toDisplayActivityInfos(context, true)
    }

    val disableInAppBrowserInSelected =
        packages.combine(disableInAppBrowserInSelectedPackages) { packages, disableInAppBrowserInSelectedPackages ->
            packages.map {
                it to (it.packageName in disableInAppBrowserInSelectedPackages)
            }.sortByValueAndName().toMap()
        }

    fun saveInAppBrowserDisableInSelected(
        activityInfoState: InAppBrowserDisableInSelected
    ) = ioLaunch {
        activityInfoState.forEach { (activityInfo, enabled) ->
            repository.insertOrDelete(enabled, activityInfo.packageName)
        }
    }
}

typealias InAppBrowserDisableInSelected = MutableMap<DisplayActivityInfo, Boolean>