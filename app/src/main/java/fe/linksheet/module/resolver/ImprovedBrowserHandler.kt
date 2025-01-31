package fe.linksheet.module.resolver

import android.content.pm.ResolveInfo
import fe.linksheet.extension.android.componentName
import fe.linksheet.extension.android.activityDescriptor

class ImprovedBrowserHandler(
    private val autoLaunchSingleBrowserExperiment: () -> Boolean
) {
    fun filterBrowsers(
        config: BrowserModeConfigHelper,
        browsers: Map<String, ResolveInfo>,
        resolveList: List<ResolveInfo>,
    ): FilteredBrowserList {
        val nonBrowsers = getAllNonBrowsers(browsers, resolveList)

        if (autoLaunchSingleBrowserExperiment()) {
            AutoLaunchSingleBrowserExperiment.handle(config, nonBrowsers, browsers)?.let { result ->
                return result
            }
        }

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
            map[uriViewActivity.activityInfo.activityDescriptor] = uriViewActivity
        }

        for ((_, resolveInfo) in browsers) {
            map.remove(resolveInfo.activityInfo.activityDescriptor)
        }

        return map.values.toList()
    }
}
