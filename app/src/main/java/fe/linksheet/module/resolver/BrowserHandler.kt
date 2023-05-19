package fe.linksheet.module.resolver

import android.content.pm.ResolveInfo
import com.tasomaniac.openwith.resolver.BrowserResolver
import fe.linksheet.extension.mapToSet
import fe.linksheet.extension.toPackageKeyedMap
import fe.linksheet.module.preference.OptionTypeMapper
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.repository.WhitelistedBrowserRepository
import kotlinx.coroutines.flow.first

class BrowserHandler(
    val preferenceRepository: PreferenceRepository,
    private val whitelistedBrowsersRepository: WhitelistedBrowserRepository,
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

    suspend fun handleBrowsers(
        browserMode: BrowserMode,
        selectedBrowser: String?,
        resolveList: MutableList<ResolveInfo>,
    ): Pair<BrowserMode, ResolveInfo?> {
        val browsers = BrowserResolver.queryPackageKeyedBrowsers()
        addAllBrowsersToResolveList(browsers, resolveList)

        return when (browserMode) {
            is BrowserMode.AlwaysAsk -> browserMode to null
            is BrowserMode.None -> {
                removeBrowsers(browsers, resolveList)
                browserMode to null
            }

            is BrowserMode.SelectedBrowser -> {
                val browserResolveInfo = browsers[selectedBrowser]
                if (browserResolveInfo != null) {
                    removeBrowsers(browsers, resolveList, setOf(selectedBrowser!!))
                    browserMode to browserResolveInfo
                }

                browserMode to null
            }

            is BrowserMode.Whitelisted -> {
                val whitelistedBrowsers = whitelistedBrowsersRepository.getAll().first().mapToSet {
                    it.packageName
                }

                removeBrowsers(browsers, resolveList, whitelistedBrowsers)
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
