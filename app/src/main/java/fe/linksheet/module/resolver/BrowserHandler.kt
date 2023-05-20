package fe.linksheet.module.resolver

import android.content.pm.ResolveInfo
import fe.linksheet.extension.mapToSet
import fe.linksheet.extension.toPackageKeyedMap
import fe.linksheet.module.database.dao.base.PackageEntityCreator
import fe.linksheet.module.database.dao.base.WhitelistedBrowsersDao
import fe.linksheet.module.database.entity.WhitelistedBrowser
import fe.linksheet.module.preference.OptionTypeMapper
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.repository.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.repository.base.WhitelistedBrowsersRepository
import kotlinx.coroutines.flow.first
import timber.log.Timber

class BrowserHandler(
    val preferenceRepository: PreferenceRepository,
    private val browserResolver: BrowserResolver,
) {
    sealed class BrowserMode(val value: String) {
        object None : BrowserMode("none")
        object AlwaysAsk : BrowserMode("always_ask")
        object SelectedBrowser : BrowserMode("browser")
        object Whitelisted : BrowserMode("whitelisted")

        companion object Companion : OptionTypeMapper<BrowserMode, String>(
            { it.value }, { arrayOf(None, AlwaysAsk, SelectedBrowser, Whitelisted) }
        )
    }

    suspend fun <T : WhitelistedBrowser<T>, C : PackageEntityCreator<T>, D : WhitelistedBrowsersDao<T, C>> handleBrowsers(
        browserMode: BrowserMode,
        selectedBrowser: String?,
        repository: WhitelistedBrowsersRepository<T, C, D>,
        resolveList: MutableList<ResolveInfo>,
    ): Pair<BrowserMode, ResolveInfo?> {
        val browsers = browserResolver.queryPackageKeyedBrowsers()
        addAllBrowsersToResolveList(browsers, resolveList)
        Timber.tag("BrowserHandler").d("Browsers=$browsers, selectedBrowser=$selectedBrowser, resolveList=$resolveList")

        return when (browserMode) {
            is BrowserMode.AlwaysAsk -> browserMode to null
            is BrowserMode.None -> {
                removeBrowsers(browsers, resolveList)
                browserMode to null
            }

            is BrowserMode.SelectedBrowser -> {
                val browserResolveInfo = browsers[selectedBrowser]
                Timber.tag("BrowserHandler").d("BrowserResolveInfo=$browserResolveInfo")

                if (browserResolveInfo != null) {
                    removeBrowsers(browsers, resolveList, setOf(selectedBrowser!!))
                }

                browserMode to browserResolveInfo
            }

            is BrowserMode.Whitelisted -> {
                removeBrowsers(browsers, resolveList, repository.getAll().first().mapToSet {
                    it.packageName
                })

                browserMode to null
            }
        }
    }

    private fun removeBrowsers(
        browsers: Map<String, ResolveInfo>,
        currentResolveList: MutableList<ResolveInfo>,
        exceptPackages: Set<String> = emptySet()
    ) {
        currentResolveList.removeAll { resolve ->
            resolve.activityInfo.packageName !in exceptPackages && browsers.containsKey(resolve.activityInfo.packageName)
        }
    }

    private fun addAllBrowsersToResolveList(
        browsers: Map<String, ResolveInfo>,
        currentResolveList: MutableList<ResolveInfo>
    ) {
        val resolvedInfos = currentResolveList.toPackageKeyedMap()
        browsers.forEach { (`package`, resolveInfo) ->
            if (!resolvedInfos.containsKey(`package`)) {
                currentResolveList.add(resolveInfo)
            }
        }
    }
}
