package com.tasomaniac.openwith.resolver

import android.content.pm.ResolveInfo
import fe.linksheet.activity.bottomsheet.BottomSheetViewModel
import fe.linksheet.extension.mapToSet
import fe.linksheet.extension.toPackageKeyedMap
import fe.linksheet.module.preference.OptionTypeMapper
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.Preferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object BrowserHandler : KoinComponent {
    private val preferenceRepository by inject<PreferenceRepository>()

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
        resolveList: MutableList<ResolveInfo>,
        viewModel: BottomSheetViewModel,
    ): Pair<BrowserMode, ResolveInfo?> {
        val browsers = BrowserResolver.queryPackageKeyedBrowsers()
        addAllBrowsersToResolveList(browsers, resolveList)

        return when (val mode = preferenceRepository.get(Preferences.browserMode)) {
            is BrowserMode.AlwaysAsk -> mode to null
            is BrowserMode.None -> {
                removeBrowsers(browsers, resolveList)
                mode to null
            }

            is BrowserMode.SelectedBrowser -> {
                val selectedBrowser = preferenceRepository.getString(Preferences.selectedBrowser)

                val browserResolveInfo = browsers[selectedBrowser]
                if (browserResolveInfo != null) {
                    removeBrowsers(browsers, resolveList, setOf(selectedBrowser!!))
                    mode to browserResolveInfo
                }

                mode to null
            }

            is BrowserMode.Whitelisted -> {
                val whitelistedBrowsers = viewModel.getWhiteListedBrowsers().mapToSet {
                    it.packageName
                }

                removeBrowsers(browsers, resolveList, whitelistedBrowsers)
                mode to null
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
