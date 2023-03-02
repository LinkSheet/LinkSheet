package com.tasomaniac.openwith.resolver

import android.content.Context
import android.content.pm.ResolveInfo
import android.os.Build
import android.util.Log
import com.tasomaniac.openwith.data.LinkSheetDatabase
import com.tasomaniac.openwith.extension.componentName
import com.tasomaniac.openwith.extension.isEqualTo
import fe.linksheet.activity.bottomsheet.BottomSheetViewModel
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.StringPersister
import fe.linksheet.module.preference.StringReader
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object BrowserHandler : KoinComponent {
    private val preferenceRepository by inject<PreferenceRepository>()

    sealed class BrowserMode(val value: String) {
        object None : BrowserMode("none")
        object AlwaysAsk : BrowserMode("always_ask")
        object SelectedBrowser : BrowserMode("browser")
        object Whitelisted : BrowserMode("whitelisted")

        companion object {
            private val readerOptions by lazy {
                listOf(None, AlwaysAsk, SelectedBrowser, Whitelisted)
            }

            val reader: StringReader<BrowserMode> = { value ->
                readerOptions.find { it.value == value }
            }

            val persister: StringPersister<BrowserMode> = { it.value }
        }
    }

    suspend fun handleBrowsers(
        context: Context,
        currentResolveList: MutableList<ResolveInfo>,
        viewModel: BottomSheetViewModel,
    ): Pair<BrowserMode, ResolveInfo?> {
        val browsers = BrowserResolver.queryBrowsers(context)
        addAllBrowsers(browsers, currentResolveList)

        val mode = preferenceRepository.getString(
            PreferenceRepository.browserMode,
            BrowserMode.persister,
            BrowserMode.reader
        )!!

        when (mode) {
            is BrowserMode.None -> {
                removeBrowsers(browsers, currentResolveList)
                return mode to null
            }

            is BrowserMode.SelectedBrowser -> {
                val selectedBrowser =
                    preferenceRepository.getString(PreferenceRepository.selectedBrowser)
                val found = browsers.find {
                    it.activityInfo.packageName == selectedBrowser
                }

                if (found != null) {
                    removeBrowsers(browsers, currentResolveList)
                    currentResolveList.add(found)

                    return mode to found
                }
            }

            is BrowserMode.Whitelisted -> {
                val whitelistedBrowsers = viewModel.getWhiteListedBrowsers()

                removeBrowsers(browsers, currentResolveList)
                currentResolveList.addAll(whitelistedBrowsers.mapNotNull { whitelistedBrowser ->
                    browsers.find { browser -> browser.activityInfo.packageName == whitelistedBrowser.packageName }
                })
            }
            else -> {}
        }

        return mode to null
    }

    private fun removeBrowsers(
        browsers: Set<ResolveInfo>,
        currentResolveList: MutableList<ResolveInfo>
    ) {
        val toRemove = currentResolveList.filter { resolve ->
            browsers.find { browser -> resolve.activityInfo.packageName == browser.activityInfo.packageName } != null
        }
        currentResolveList.removeAll(toRemove)
    }

    private fun addAllBrowsers(
        browsers: Set<ResolveInfo>,
        currentResolveList: MutableList<ResolveInfo>
    ) {
        val initialList = currentResolveList.toSet()
        browsers.forEach { browser ->
            val notFound =
                initialList.find { it.activityInfo.packageName == browser.activityInfo.packageName } == null
            if (notFound) {
                currentResolveList.add(browser)
            }
        }
    }
}
