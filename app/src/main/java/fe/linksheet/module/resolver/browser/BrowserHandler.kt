package fe.linksheet.module.resolver.browser

import android.content.pm.ResolveInfo
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.extension.android.toPackageKeyedMap
import fe.linksheet.lib.flavors.LinkSheetApp
import fe.linksheet.module.database.dao.base.PackageEntityCreator
import fe.linksheet.module.database.dao.base.WhitelistedBrowsersDao
import fe.linksheet.module.database.entity.whitelisted.WhitelistedBrowser
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.redactor.Redactable
import fe.linksheet.module.redactor.Redactor
import fe.linksheet.module.repository.whitelisted.WhitelistedBrowsersRepository
import fe.linksheet.module.resolver.BrowserResolver
import fe.stringbuilder.util.commaSeparated
import kotlinx.coroutines.flow.first

class BrowserHandler(
    val preferenceRepository: AppPreferenceRepository,
    private val browserResolver: BrowserResolver,
) {
    data class BrowserModeInfo(val browserMode: BrowserMode, val resolveInfo: ResolveInfo?) :
        Redactable<BrowserModeInfo> {
        override fun process(builder: StringBuilder, redactor: Redactor): StringBuilder {
            return builder.commaSeparated {
//                item {
//                    append("mode=").append(browserMode)
//                }
//                itemNotNull(resolveInfo) {
//                    redactor.process(this, resolveInfo, HashProcessor.ResolveInfoProcessor, "resolveInfo=")
//                }
            }
        }
    }


    private fun tryMerge(browsers: Map<String, ResolveInfo>, resolveList: List<ResolveInfo>): List<ResolveInfo> {
        val merged = mutableMapOf<String, ResolveInfo>()
        for (resolveInfo in resolveList) {
            merged[resolveInfo.activityInfo.packageName] = resolveInfo
        }

        merged.putAll(browsers)
        return merged.values.toList()
    }

    suspend fun <T : WhitelistedBrowser<T>, C : PackageEntityCreator<T>, D : WhitelistedBrowsersDao<T, C>> handleBrowsers(
        browserMode: BrowserMode,
        selectedBrowser: String?,
        repository: WhitelistedBrowsersRepository<T, C, D>,
        resolveList: MutableList<ResolveInfo>,
    ): Pair<BrowserModeInfo, MutableList<ResolveInfo>> {
        // TODO: this should be refactored, passing a list of all applications which can handle the
        //  intent (including browsers), then QUERYING ALL BROWSERS again just to remove them from
        //  the list by comparing each element is just plain stupid, lol
        val browsers = browserResolver.queryBrowsers()
        addAllBrowsersToResolveList(browsers, resolveList)

        resolveList.removeIf { LinkSheetApp.Compat.isApp(it.activityInfo.packageName) != null }

        return when (browserMode) {
            is BrowserMode.AlwaysAsk -> BrowserModeInfo(browserMode, null)
            is BrowserMode.None -> {
                removeBrowsers(browsers, resolveList)
                BrowserModeInfo(browserMode, null)
            }

            is BrowserMode.SelectedBrowser -> {
                val browserResolveInfo = browsers[selectedBrowser]

                if (browserResolveInfo != null) {
                    removeBrowsers(browsers, resolveList, setOf(selectedBrowser!!))
                }

                BrowserModeInfo(browserMode, browserResolveInfo)
            }

            is BrowserMode.Whitelisted -> {
                removeBrowsers(browsers, resolveList, repository.getAll().first().mapToSet {
                    it.packageName
                })

                BrowserModeInfo(browserMode, null)
            }
        } to resolveList
    }

    // TODO: Refactor, old query manager doesn't return separated lists for apps and browsers -> remove browsers from app list,
    //  but only if we have at least one browser
    private fun tryRemoveBrowsers(
        browsers: Map<String, ResolveInfo>,
        resolveList: List<ResolveInfo>,
        exceptPackages: Set<String> = emptySet(),
    ): List<ResolveInfo> {
        val canRemove = exceptPackages.isNotEmpty() || resolveList.size > browsers.size
        if (!canRemove) return resolveList

        // if exceptPackages.size == 0 && currentResolveList > browsers means we are in BrowserMode.None,
        // and have at least one native app which can handle the link (if currentResolveList is equal
        // to browsers, no native app which means we should not remove the browsers since that would
        // mean the user is presented with a bottom sheet which loads forever)

        return resolveList.filterNot { resolve ->
            resolve.activityInfo.packageName !in exceptPackages && browsers.containsKey(resolve.activityInfo.packageName)
        }
    }


    private fun removeBrowsers(
        browsers: Map<String, ResolveInfo>,
        currentResolveList: MutableList<ResolveInfo>,
        exceptPackages: Set<String> = emptySet(),
    ) {
        if (exceptPackages.isNotEmpty() || currentResolveList.size > browsers.size) {
            // if exceptPackages.size == 0 && currentResolveList > browsers means we are in BrowserMode.None,
            // and have at least one native app which can handle the link (if currentResolveList is equal
            // to browsers, no native app which means we should not remove the browsers since that would
            // mean the user is presented with a bottom sheet which loads forever)
            currentResolveList.removeAll { resolve ->
                resolve.activityInfo.packageName !in exceptPackages && browsers.containsKey(resolve.activityInfo.packageName)
            }
        }
    }

    private fun addAllBrowsersToResolveList(
        browsers: Map<String, ResolveInfo>,
        currentResolveList: MutableList<ResolveInfo>,
    ) {
        val resolvedInfos = currentResolveList.toPackageKeyedMap()
        browsers.forEach { (pkg, resolveInfo) ->
            if (!resolvedInfos.containsKey(pkg)) {
                currentResolveList.add(resolveInfo)
            }
        }
    }
}
