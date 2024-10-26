package fe.linksheet.experiment.improved.resolver

import android.content.pm.ResolveInfo
import fe.linksheet.experiment.improved.resolver.browser.BrowserModeConfigHelper

object AutoLaunchSingleBrowserExperiment {
    fun handle(
        config: BrowserModeConfigHelper,
        nonBrowsers: List<ResolveInfo>,
        browsers: Map<String, ResolveInfo>,
    ): FilteredBrowserList? {
        if (nonBrowsers.isNotEmpty()) return null

        if (browsers.size == 1) {
            // If single browser -> return
            return createResult(config, nonBrowsers, browsers.values.single())
        }

        if (config is BrowserModeConfigHelper.SelectedBrowser) {
            // If we have a selected browser, try to lookup it up, then return
            return browsers[config.selectedBrowser]?.let { createResult(config, nonBrowsers, it) }
        }

        if (config is BrowserModeConfigHelper.Whitelisted) {
            // If we have a single whitelisted browser, try to lookup it up, then return
            return config.whitelistedPackages
                ?.singleOrNull()?.let { browsers[it] }
                ?.let { createResult(config, nonBrowsers, it) }
        }

        return null
    }

    private fun createResult(
        config: BrowserModeConfigHelper,
        nonBrowsers: List<ResolveInfo>,
        browser: ResolveInfo,
    ): FilteredBrowserList {
        return FilteredBrowserList(
            config.mode,
            listOf(browser),
            nonBrowsers,
            isSingleOption = true,
            noBrowsersOnlySingleApp = false
        )
    }
}
