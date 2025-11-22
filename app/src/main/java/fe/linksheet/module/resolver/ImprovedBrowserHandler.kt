package fe.linksheet.module.resolver

import android.content.ComponentName
import android.content.pm.ResolveInfo
import fe.composekit.extension.componentName
import fe.linksheet.extension.android.activityDescriptor
import fe.linksheet.module.repository.whitelisted.createWhitelistedBrowserInfo

class ImprovedBrowserHandler(
    private val autoLaunchSingleBrowserExperiment: () -> Boolean
) {
    companion object {
        internal fun getBrowser(browsers: List<ResolveInfo>, selectedBrowser: String): ResolveInfo? {
            // selectedBrowser used to store the package name only, but since a browser might expose multiple activities which
            // can handle a url, we need to store the component as well -> gracefully handle either, migration is only done
            // once the user changes the selected browser/component from within settings
            val cmp = ComponentName.unflattenFromString(selectedBrowser)
            if (cmp != null) {
                return browsers.firstOrNull { it.activityInfo.componentName == cmp }
            }

            return browsers.firstOrNull { it.resolvePackageName == selectedBrowser }
        }
    }

    fun filterBrowsers(
        config: BrowserModeConfigHelper,
        browsers: List<ResolveInfo>,
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
                browsers,
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
                val browserResolveInfo = config.selectedBrowser?.let { getBrowser(browsers, it) }
                val hasInfo = browserResolveInfo != null
                val list = if (hasInfo) listOf(browserResolveInfo) else emptyList()

//                // TODO: Need to use merged here since resolvedList might contain ResolveInfos also present in browsers
//                // TODO: Do we really need to use the component?
//                val isSingleOption = nonBrowsers.isEmpty()
//                        && browsers.size == 1
//                        && browsers.values.singleOrNull()?.activityInfo?.componentName == browserResolveInfo?.activityInfo?.componentName
                FilteredBrowserList(
                    config.mode,
                    list,
                    nonBrowsers,
                    isSingleOption = hasInfo
                )
            }

            is BrowserModeConfigHelper.Whitelisted -> {
                val whitelistedPackages = config.whitelistedPackages

                // TODO: If whitelisted empty, show all browsers; Does that make sense?
                val whitelisted = when {
                    !whitelistedPackages.isNullOrEmpty() -> {
                        val info = createWhitelistedBrowserInfo(whitelistedPackages)
                        browsers.filter {
                            it.activityInfo.componentName in info.cmps || it.resolvePackageName in info.pkgs
                        }
                    }

                    else -> browsers
                }

                FilteredBrowserList(config.mode, whitelisted, nonBrowsers)
            }
        }
    }


    private fun getAllNonBrowsers(
        browsers: List<ResolveInfo>,
        resolveList: List<ResolveInfo>,
    ): List<ResolveInfo> {
        val map = mutableMapOf<String, ResolveInfo>()
        for (uriViewActivity in resolveList) {
            map[uriViewActivity.activityInfo.activityDescriptor] = uriViewActivity
        }

        for (resolveInfo in browsers) {
            map.remove(resolveInfo.activityInfo.activityDescriptor)
        }

        return map.values.toList()
    }
}

