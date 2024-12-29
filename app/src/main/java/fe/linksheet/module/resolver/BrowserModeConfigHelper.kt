package fe.linksheet.module.resolver

import fe.linksheet.module.resolver.browser.BrowserMode


sealed class BrowserModeConfigHelper(val mode: BrowserMode) {
    data object None : BrowserModeConfigHelper(BrowserMode.None)

    class SelectedBrowser(
        val selectedBrowser: String?,
    ) : BrowserModeConfigHelper(BrowserMode.SelectedBrowser)

    class Whitelisted(
        val whitelistedPackages: Set<String>?
    ) : BrowserModeConfigHelper(BrowserMode.Whitelisted)

    data object AlwaysAsk : BrowserModeConfigHelper(BrowserMode.AlwaysAsk)
}
