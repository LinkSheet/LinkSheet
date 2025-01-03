package fe.linksheet.module.viewmodel


import android.app.Application
import fe.linksheet.extension.android.launchIO
import fe.linksheet.module.app.ActivityAppInfoSortCompat
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.BrowserResolver
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.module.viewmodel.base.BrowserCommonSelected
import fe.linksheet.module.viewmodel.base.BrowserCommonViewModel
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
        preferenceRepository.asState(AppPreferences.unifiedPreferredBrowser)

    private var browserMode = preferenceRepository.asState(AppPreferences.browserMode)

    @OptIn(SensitivePreference::class)
    private var selectedBrowser = preferenceRepository.asState(AppPreferences.selectedBrowser)

    private var inAppBrowserMode = preferenceRepository.asState(AppPreferences.inAppBrowserMode)

    @OptIn(SensitivePreference::class)
    private var selectedInAppBrowser = preferenceRepository.asState(
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
        packages: Flow<Set<String>>,
    ) = browsers.combine(packages) { browsers, pkgs ->
        ActivityAppInfoSortCompat.mapBrowserState(browsers, pkgs)
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

    fun updateSelectedBrowser(selectedBrowserPackage: String) = launchIO {
        val state = browserModeState.first()
        val selected = selectedBrowserState.first()

        state(BrowserMode.SelectedBrowser)
        selected(selectedBrowserPackage)
    }
}
