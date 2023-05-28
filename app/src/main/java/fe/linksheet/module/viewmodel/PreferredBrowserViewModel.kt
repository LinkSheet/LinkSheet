package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.extension.ioLaunch
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getBooleanState
import fe.android.preference.helper.compose.getState
import fe.android.preference.helper.compose.getStringState
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.repository.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.BrowserHandler
import fe.linksheet.module.resolver.BrowserResolver
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.resolver.DisplayActivityInfo.Companion.sortByValueAndName
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PreferredBrowserViewModel(
    val context: Application,
    private val browserResolver: BrowserResolver,
    private val normalBrowsersRepository: WhitelistedNormalBrowsersRepository,
    private val inAppBrowsersRepository: WhitelistedInAppBrowsersRepository,
    preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {

    val type = MutableStateFlow(BrowserType.Normal)

    val unifiedPreferredBrowser =
        preferenceRepository.getBooleanState(Preferences.unifiedPreferredBrowser)

    private var browserMode = preferenceRepository.getState(Preferences.browserMode)
    private var selectedBrowser = preferenceRepository.getStringState(Preferences.selectedBrowser)

    private var inAppBrowserMode = preferenceRepository.getState(Preferences.inAppBrowserMode)
    private var selectedInAppBrowser = preferenceRepository.getStringState(
        Preferences.selectedInAppBrowser
    )

    private val whitelistedNormalBrowsersPackages = normalBrowsersRepository.getPackageSet()
    private val whitelistedInAppBrowsersPackages = inAppBrowsersRepository.getPackageSet()

    val browsers = flowOfLazy {
        browserResolver.queryDisplayActivityInfoBrowsers(true)
    }

    private val whitelistedNormalBrowsers =
        getWhitelistedBrowsers(whitelistedNormalBrowsersPackages)
    private val whitelistedInAppBrowsers = getWhitelistedBrowsers(whitelistedInAppBrowsersPackages)

    private fun getWhitelistedBrowsers(
        packages: Flow<HashSet<String>>
    ) = browsers.combine(packages) { browsers, pkgs ->
        browsers.map {
            it to (it.packageName in pkgs)
        }.sortByValueAndName().toMap()
    }

    val browserModeState = type.map {
        when (it) {
            BrowserType.Normal -> browserMode
            BrowserType.InApp -> inAppBrowserMode
        }
    }

    val selectedBrowserState = type.map {
        when (it) {
            BrowserType.Normal -> selectedBrowser
            BrowserType.InApp -> selectedInAppBrowser
        }
    }

    val repository = type.map {
        when (it) {
            BrowserType.Normal -> normalBrowsersRepository
            BrowserType.InApp -> inAppBrowsersRepository
        }
    }

    val whitelistedBrowsers = type.map {
        when (it) {
            BrowserType.Normal -> whitelistedNormalBrowsers
            BrowserType.InApp -> whitelistedInAppBrowsers
        }
    }

    enum class BrowserType {
        Normal, InApp
    }

    fun saveWhitelistedBrowsers(
        activityInfoState: WhitelistedBrowsers
    ) = ioLaunch {
        val repo = repository.first()
        activityInfoState.forEach { (activityInfo, enabled) ->
            repo.insertOrDelete(enabled, activityInfo.packageName)
        }
    }

    fun updateSelectedBrowser(
        selectedBrowserPackage: String,
    ) = ioLaunch {
        val state = browserModeState.first()
        val selected = selectedBrowserState.first()

        updateState(state, BrowserHandler.BrowserMode.SelectedBrowser)
        updateState(selected, selectedBrowserPackage)
    }
}

typealias WhitelistedBrowsers = MutableMap<DisplayActivityInfo, Boolean>
