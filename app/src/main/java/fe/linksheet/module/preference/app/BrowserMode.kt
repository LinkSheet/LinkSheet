package fe.linksheet.module.preference.app

import app.linksheet.api.PreferenceRegistry
import app.linksheet.api.mapped
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.resolver.browser.BrowserMode

class BrowserMode(registry: PreferenceRegistry) {
    val autoLaunchSingleBrowser = registry.boolean("auto_launch_single_browser")
    @SensitivePreference
    val selectedBrowser = registry.string("selected_browser")
    val browserMode = registry.mapped("browser_mode", BrowserMode.AlwaysAsk, BrowserMode)

    @SensitivePreference
    val selectedInAppBrowser = registry.string("selected_in_app_browser")
    val inAppBrowserMode = registry.mapped("in_app_browser_mode", BrowserMode.AlwaysAsk, BrowserMode)

    val unifiedPreferredBrowser = registry.boolean("unified_preferred_browser", true)

    val inAppBrowserSettings = registry.mapped(
        "in_app_browser_setting",
        InAppBrowserHandler.InAppBrowserMode.UseAppSettings,
        InAppBrowserHandler.InAppBrowserMode
    )
}
