package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.extension.android.launchIO
import fe.linksheet.module.preference.AppPreferenceRepository

import fe.android.preference.helper.compose.getBooleanState
import fe.android.preference.helper.compose.getState
import fe.android.preference.helper.compose.getStringState
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.BrowserHandler
import fe.linksheet.module.resolver.BrowserResolver
import fe.linksheet.module.viewmodel.base.BrowserCommonSelected
import fe.linksheet.module.viewmodel.base.BrowserCommonViewModel
import fe.linksheet.resolver.DisplayActivityInfo.Companion.sortByValueAndName
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PreferredBrowserViewModel(
    context: Application,
    private val browserResolver: BrowserResolver,
    private val normalBrowsersRepository: WhitelistedNormalBrowsersRepository,
    private val inAppBrowsersRepository: WhitelistedInAppBrowsersRepository,
    preferenceRepository: AppPreferenceRepository
) : BrowserCommonViewModel(context, preferenceRepository) {

    val type = MutableStateFlow(BrowserType.Normal)

    val unifiedPreferredBrowser =
        preferenceRepository.getBooleanState(AppPreferences.unifiedPreferredBrowser)

    private var browserMode = preferenceRepository.getState(AppPreferences.browserMode)
    private var selectedBrowser = preferenceRepository.getStringState(AppPreferences.selectedBrowser)

    private var inAppBrowserMode = preferenceRepository.getState(AppPreferences.inAppBrowserMode)
    private var selectedInAppBrowser = preferenceRepository.getStringState(
        AppPreferences.selectedInAppBrowser
    )

    private val whitelistedNormalBrowsersPackages = normalBrowsersRepository.getPackageSet()
    private val whitelistedInAppBrowsersPackages = inAppBrowsersRepository.getPackageSet()

    val browsers = flowOfLazy {
        browserResolver.queryDisplayActivityInfoBrowsers(true)
    }

    private val whitelistedNormalBrowsers = getWhitelistedBrowsers(
        whitelistedNormalBrowsersPackages
    )

    private val whitelistedInAppBrowsers = getWhitelistedBrowsers(whitelistedInAppBrowsersPackages)

    private fun getWhitelistedBrowsers(
        packages: Flow<Set<String>>
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

    override fun items() = type.map {
        when (it) {
            BrowserType.Normal -> whitelistedNormalBrowsers
            BrowserType.InApp -> whitelistedInAppBrowsers
        }.first()
    }

    enum class BrowserType {
        Normal, InApp
    }

    override fun save(selected: BrowserCommonSelected) = launchIO {
        val repo = repository.first()
        selected.forEach { (activityInfo, enabled) ->
            repo.insertOrDelete(enabled, activityInfo.packageName)
        }
    }

    fun updateSelectedBrowser(
        selectedBrowserPackage: String,
    ) = launchIO {
        val state = browserModeState.first()
        val selected = selectedBrowserState.first()

        updateState(state, BrowserHandler.BrowserMode.SelectedBrowser)
        updateState(selected, selectedBrowserPackage)
    }
}
