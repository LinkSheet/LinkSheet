@file:OptIn(RefactorGlue::class)

package fe.linksheet.module.viewmodel


import android.app.Application
import app.linksheet.feature.app.ActivityAppInfo
import app.linksheet.feature.app.ActivityAppInfoStatus
import fe.linksheet.extension.android.launchIO
import fe.linksheet.feature.app.ActivityAppInfoSortGlue
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.repository.whitelisted.WhitelistedBrowserInfo
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.BrowserResolver
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.module.viewmodel.base.BrowserCommonViewModel
import fe.linksheet.util.RefactorGlue
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.*

class PreferredBrowserViewModel(
    context: Application,
    private val browserResolver: BrowserResolver,
    private val normalBrowsersRepository: WhitelistedNormalBrowsersRepository,
    private val inAppBrowsersRepository: WhitelistedInAppBrowsersRepository,
    preferenceRepository: AppPreferenceRepository,
) : BrowserCommonViewModel(context, preferenceRepository) {

    val type = MutableStateFlow(BrowserType.Normal)

    val unifiedPreferredBrowser =
        preferenceRepository.asViewModelState(AppPreferences.unifiedPreferredBrowser)

    private val browserMode = preferenceRepository.asViewModelState(AppPreferences.browserMode)

    @OptIn(SensitivePreference::class)
    private val selectedBrowser = preferenceRepository.asViewModelState(AppPreferences.selectedBrowser)

    private val inAppBrowserMode = preferenceRepository.asViewModelState(AppPreferences.inAppBrowserMode)

    @OptIn(SensitivePreference::class)
    private val selectedInAppBrowser = preferenceRepository.asViewModelState(AppPreferences.selectedInAppBrowser)

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
        packages: Flow<WhitelistedBrowserInfo>,
    ) = browsers.combine(packages) { browsers, pkgs ->
        ActivityAppInfoSortGlue.mapBrowserState(browsers, pkgs)
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

    override fun save(items: List<ActivityAppInfoStatus>?, selected: MutableMap<ActivityAppInfoStatus, Boolean>) = launchIO {
        val repo = repository.first()

        if(items != null) {
            repo.migrateState(items)
        }

        for ((status, newState) in selected) {
            repo.insertOrDelete(newState, status)
        }
    }

    fun updateSelectedBrowser(selectedBrowserPackage: ActivityAppInfo) = launchIO {
        val state = browserModeState.first()
        val selected = selectedBrowserState.first()

        state(BrowserMode.SelectedBrowser)
        selected(selectedBrowserPackage.flatComponentName)
    }
}
