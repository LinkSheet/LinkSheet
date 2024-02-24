package fe.linksheet.module.resolver

import android.content.pm.ResolveInfo
import fe.android.preference.helper.OptionTypeMapper
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.module.preference.AppPreferenceRepository

import fe.linksheet.extension.android.toPackageKeyedMap
import fe.linksheet.module.database.dao.base.PackageEntityCreator
import fe.linksheet.module.database.dao.base.WhitelistedBrowsersDao
import fe.linksheet.module.database.entity.whitelisted.WhitelistedBrowser
import fe.linksheet.module.log.hasher.HashProcessor
import fe.linksheet.module.log.hasher.LogDumpable
import fe.linksheet.module.log.hasher.LogDumpable.Companion.dumpObject
import fe.linksheet.module.log.hasher.LogHasher
import fe.linksheet.module.repository.whitelisted.WhitelistedBrowsersRepository
import fe.stringbuilder.util.commaSeparated
import kotlinx.coroutines.flow.first

class BrowserHandler(
    val preferenceRepository: AppPreferenceRepository,
    private val browserResolver: BrowserResolver,
) {
    sealed class BrowserMode(val value: String) : LogDumpable {
        data object None : BrowserMode("none")
        data object AlwaysAsk : BrowserMode("always_ask")
        data object SelectedBrowser : BrowserMode("browser")
        data object Whitelisted : BrowserMode("whitelisted")

        companion object : OptionTypeMapper<BrowserMode, String>(
            { it.value }, { arrayOf(None, AlwaysAsk, SelectedBrowser, Whitelisted) }
        )

        override fun dump(stringBuilder: StringBuilder, hasher: LogHasher): StringBuilder {
            return hasher.hash(stringBuilder, value, HashProcessor.NoOpProcessor)
        }
    }

    data class BrowserModeInfo(val browserMode: BrowserMode, val resolveInfo: ResolveInfo?) : LogDumpable {
        override fun dump(stringBuilder: StringBuilder, hasher: LogHasher) = stringBuilder.commaSeparated {
            item { dumpObject("mode=", this, hasher, browserMode) }
            itemNotNull(resolveInfo) {
                dumpObject("resolveInfo=", this, hasher, resolveInfo)
            }
        }
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

        resolveList.removeIf {
            it.activityInfo.packageName == "fe.linksheet.compat.debug"
        }

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

    private fun removeBrowsers(
        browsers: Map<String, ResolveInfo>,
        currentResolveList: MutableList<ResolveInfo>,
        exceptPackages: Set<String> = emptySet()
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
        currentResolveList: MutableList<ResolveInfo>
    ) {
        val resolvedInfos = currentResolveList.toPackageKeyedMap()
        browsers.forEach { (pkg, resolveInfo) ->
            if (!resolvedInfos.containsKey(pkg)) {
                currentResolveList.add(resolveInfo)
            }
        }
    }
}
