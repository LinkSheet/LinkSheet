package fe.linksheet.module.resolver

import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.module.resolver.module.BrowserSettings

object IntentResolverCommon {
    suspend fun createBrowserModeConfig(
        browserSettings: BrowserSettings,
        customTab: Boolean
    ): BrowserModeConfigHelper {
        val useInAppSettings = !browserSettings.unifiedPreferredBrowser() && customTab
        val mode = when {
            useInAppSettings -> browserSettings.inAppBrowserMode()
            else -> browserSettings.browserMode()
        }

        return when (mode) {
            BrowserMode.AlwaysAsk -> BrowserModeConfigHelper.AlwaysAsk
            BrowserMode.None -> BrowserModeConfigHelper.None
            BrowserMode.SelectedBrowser -> {
                val selectedBrowser = when {
                    useInAppSettings -> browserSettings.selectedInAppBrowser()
                    else -> browserSettings.selectedBrowser()
                }
                BrowserModeConfigHelper.SelectedBrowser(selectedBrowser)
            }

            BrowserMode.Whitelisted -> {
                val whitelistedPackages = when {
                    useInAppSettings -> browserSettings.whitelistedInAppBrowserPackages()
                    else -> browserSettings.whitelistedNormalBrowserPackages()
                }
                BrowserModeConfigHelper.Whitelisted(whitelistedPackages)
            }
        }
    }
}
