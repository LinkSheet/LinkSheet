package fe.linksheet.experiment.improved.resolver.browser

import android.content.pm.ResolveInfo
import fe.linksheet.experiment.improved.resolver.FilteredBrowserList
import fe.linksheet.extension.android.componentName
import fe.linksheet.module.resolver.browser.BrowserMode


class ImprovedBrowserHandler(
) {
    fun filterBrowsers(
        config: BrowserModeConfigHelper,
        browsers: Map<String, ResolveInfo>,
        resolveList: List<ResolveInfo>,
    ): FilteredBrowserList {
        val nonBrowsers = getAllNonBrowsers(browsers, resolveList)

        return when (config) {
            is BrowserModeConfigHelper.AlwaysAsk -> FilteredBrowserList(
                config.mode,
                browsers.values.toList(),
                nonBrowsers
            )

            is BrowserModeConfigHelper.None -> {
                val noBrowsersOnlySingleApp = resolveList.size == 1 && browsers.isEmpty()
                FilteredBrowserList(
                    config.mode,
                    emptyList(),
                    nonBrowsers,
                    noBrowsersOnlySingleApp = noBrowsersOnlySingleApp
                )
            }

            is BrowserModeConfigHelper.SelectedBrowser -> {
                val browserResolveInfo = browsers[config.selectedBrowser]
                val list = if (browserResolveInfo == null) emptyList() else listOf(browserResolveInfo)

                // TODO: Need to use merged here since resolvedList might contain ResolveInfos also present in browsers
                // TODO: Do we really need to use the component?
                val isSingleOption = nonBrowsers.isEmpty()
                        && browsers.size == 1
                        && browsers.values.singleOrNull()?.activityInfo?.componentName() == browserResolveInfo?.activityInfo?.componentName()

                FilteredBrowserList(
                    config.mode,
                    list,
                    nonBrowsers,
                    isSingleOption = isSingleOption
                )
            }

            is BrowserModeConfigHelper.Whitelisted -> {
                val whitelistedPackages = config.whitelistedPackages

                // TODO: If whitelisted empty, show all browsers; Does that make sense?
                val whitelisted = if (!whitelistedPackages.isNullOrEmpty()) {
                    browsers.filter { (pkg, _) -> pkg in whitelistedPackages }
                } else browsers

                FilteredBrowserList(config.mode, whitelisted.values.toList(), nonBrowsers)
            }
        }
    }

    private fun getAllNonBrowsers(
        browsers: Map<String, ResolveInfo>,
        resolveList: List<ResolveInfo>,
    ): List<ResolveInfo> {
        val map = mutableMapOf<String, ResolveInfo>()
        for (uriViewActivity in resolveList) {
            map[uriViewActivity.activityInfo.packageName] = uriViewActivity
        }

        for ((pkg, _) in browsers) {
            map.remove(pkg)
        }

        return map.values.toList()
    }
}