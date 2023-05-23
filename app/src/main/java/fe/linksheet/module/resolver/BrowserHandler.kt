package fe.linksheet.module.resolver

import android.content.pm.ResolveInfo
import fe.linksheet.extension.mapToSet
import fe.linksheet.extension.separated
import fe.linksheet.extension.toPackageKeyedMap
import fe.linksheet.module.database.dao.base.PackageEntityCreator
import fe.linksheet.module.database.dao.base.WhitelistedBrowsersDao
import fe.linksheet.module.database.entity.WhitelistedBrowser
import fe.linksheet.module.log.HashProcessor
import fe.linksheet.module.log.LogDumpable
import fe.linksheet.module.log.LogDumpable.Companion.dumpObject
import fe.linksheet.module.log.LogHasher
import fe.linksheet.module.preference.OptionTypeMapper
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.repository.base.WhitelistedBrowsersRepository
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.lang.StringBuilder

class BrowserHandler(
    val preferenceRepository: PreferenceRepository,
    private val browserResolver: BrowserResolver,
) {
    sealed class BrowserMode(val value: String) : LogDumpable {
        object None : BrowserMode("none")
        object AlwaysAsk : BrowserMode("always_ask")
        object SelectedBrowser : BrowserMode("browser")
        object Whitelisted : BrowserMode("whitelisted")

        companion object Companion : OptionTypeMapper<BrowserMode, String>(
            { it.value }, { arrayOf(None, AlwaysAsk, SelectedBrowser, Whitelisted) }
        )

        override fun dump(
            stringBuilder: StringBuilder,
            hasher: LogHasher
        ) = hasher.hash(stringBuilder, value, HashProcessor.NoOpProcessor)

    }

    data class BrowserModeInfo(
        val browserMode: BrowserMode,
        val resolveInfo: ResolveInfo?
    ) : LogDumpable {
        override fun dump(
            stringBuilder: StringBuilder,
            hasher: LogHasher
        ) = stringBuilder.separated(",") {
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
        val browsers = browserResolver.queryPackageKeyedBrowsers()
        addAllBrowsersToResolveList(browsers, resolveList)
        Timber.tag("BrowserHandler")
            .d("Browsers=$browsers, selectedBrowser=$selectedBrowser, resolveList=$resolveList")

        return when (browserMode) {
            is BrowserMode.AlwaysAsk -> BrowserModeInfo(browserMode, null)
            is BrowserMode.None -> {
                removeBrowsers(browsers, resolveList)
                BrowserModeInfo(browserMode, null)
            }

            is BrowserMode.SelectedBrowser -> {
                val browserResolveInfo = browsers[selectedBrowser]
                Timber.tag("BrowserHandler").d("BrowserResolveInfo=$browserResolveInfo")

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
