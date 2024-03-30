package fe.linksheet.module.resolver

import android.content.pm.ResolveInfo
import fe.android.preference.helper.OptionTypeMapper
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.extension.android.componentName
import fe.linksheet.extension.android.toPackageKeyedMap
import fe.linksheet.module.database.dao.base.PackageEntityCreator
import fe.linksheet.module.database.dao.base.WhitelistedBrowsersDao
import fe.linksheet.module.database.entity.whitelisted.WhitelistedBrowser
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.redactor.Redactable
import fe.linksheet.module.redactor.Redactor
import fe.linksheet.module.repository.whitelisted.WhitelistedBrowsersRepository
import fe.linksheet.util.LinkSheetCompat
import fe.stringbuilder.util.commaSeparated
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class BrowserHandler(
    val preferenceRepository: AppPreferenceRepository,
    private val browserResolver: BrowserResolver,
) {
    sealed class BrowserMode(val value: String) {
        data object None : BrowserMode("none")
        data object AlwaysAsk : BrowserMode("always_ask")
        data object SelectedBrowser : BrowserMode("browser")
        data object Whitelisted : BrowserMode("whitelisted")

        companion object : OptionTypeMapper<BrowserMode, String>(
            { it.value }, { arrayOf(None, AlwaysAsk, SelectedBrowser, Whitelisted) }
        )

        override fun toString(): String {
            return value
        }
    }

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

    data class BrowserModeConfigHelper<T : WhitelistedBrowser<T>, C : PackageEntityCreator<T>, D : WhitelistedBrowsersDao<T, C>>(
        val browserMode: BrowserMode,
        val selectedBrowser: String?,
        val repository: WhitelistedBrowsersRepository<T, C, D>,
    )

    data class FilteredBrowserList(
        val browserMode: BrowserMode,
        val browsers: List<ResolveInfo>,
        val apps: List<ResolveInfo>,
        val isSingleOption: Boolean = false,
        val noBrowsersOnlySingleApp: Boolean = false,
    ) {

    }

    suspend fun <T : WhitelistedBrowser<T>, C : PackageEntityCreator<T>, D : WhitelistedBrowsersDao<T, C>> filterBrowsers(
        config: BrowserModeConfigHelper<T, C, D>,
        browsers: Map<String, ResolveInfo>,
        resolveList: List<ResolveInfo>,
    ): FilteredBrowserList {
        val nonBrowsers = getAllNonBrowsers(browsers, resolveList)

        return when (config.browserMode) {
            is BrowserMode.AlwaysAsk -> FilteredBrowserList(
                config.browserMode,
                browsers.values.toList(),
                nonBrowsers
            )

            is BrowserMode.None -> {
                val noBrowsersOnlySingleApp = resolveList.size == 1 && browsers.isEmpty()
                FilteredBrowserList(
                    config.browserMode,
                    emptyList(),
                    nonBrowsers,
                    noBrowsersOnlySingleApp = noBrowsersOnlySingleApp
                )
            }

            is BrowserMode.SelectedBrowser -> {
                val browserResolveInfo = browsers[config.selectedBrowser]
                val list = if (browserResolveInfo == null) emptyList() else listOf(browserResolveInfo)

                // TODO: Need to use merged here since resolvedList might contain ResolveInfos also present in browsers
                // TODO: Do we really need to use the component?
                val isSingleOption = nonBrowsers.isEmpty()
                        && browsers.size == 1
                        && browsers.values.singleOrNull()?.activityInfo?.componentName() == browserResolveInfo?.activityInfo?.componentName()

                FilteredBrowserList(
                    config.browserMode,
                    list,
                    nonBrowsers,
                    isSingleOption = isSingleOption
                )
            }

            is BrowserMode.Whitelisted -> {
                val whitelistedPackages = config.repository.getAll().firstOrNull()?.mapToSet { it.packageName }

                // TODO: If whitelisted empty, show all browsers; Does that make sense?
                val whitelisted = if (!whitelistedPackages.isNullOrEmpty()) {
                    browsers.filter { (pkg, _) -> pkg in whitelistedPackages }
                } else browsers

                FilteredBrowserList(config.browserMode, whitelisted.values.toList(), nonBrowsers)
            }
        }
    }

    private fun getAllNonBrowsers(
        browsers: Map<String, ResolveInfo>,
        resolveList: List<ResolveInfo>,
    ): List<ResolveInfo> {
        val map = mutableMapOf<String, ResolveInfo>()
        for (resolveInfo in resolveList) {
            map[resolveInfo.activityInfo.packageName] = resolveInfo
        }

        for ((pkg, _) in browsers) {
            map.remove(pkg)
        }

        return map.values.toList()
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
    ): BrowserModeInfo {
        // TODO: this should be refactored, passing a list of all applications which can handle the
        //  intent (including browsers), then QUERYING ALL BROWSERS again just to remove them from
        //  the list by comparing each element is just plain stupid, lol
        val browsers = browserResolver.queryBrowsers()
        addAllBrowsersToResolveList(browsers, resolveList)

        resolveList.removeIf { LinkSheetCompat.isCompat(it) }


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
        }
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
