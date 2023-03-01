package com.tasomaniac.openwith.resolver

import android.content.Context
import android.content.pm.ResolveInfo
import android.os.Build
import android.util.Log
import com.tasomaniac.openwith.extension.componentName
import com.tasomaniac.openwith.extension.isEqualTo
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

        companion object {
            private val readerOptions by lazy {
                listOf(None, AlwaysAsk, SelectedBrowser)
            }

            val reader: StringReader<BrowserMode> = { value ->
                readerOptions.find { it.value == value }
            }

            val persister: StringPersister<BrowserMode> = { it.value }
        }
    }

    /**
     * First add all browsers into the list if the device is > [Build.VERSION_CODES.M]
     *
     * Then depending on the browser preference,
     *
     * - Remove all browsers
     * - Only put the selected browser
     *
     * If the selected browser is not found, fallback to [BrowserMode.AlwaysAsk]
     *
     */
    fun handleBrowsers(context: Context, currentResolveList: MutableList<ResolveInfo>): Pair<BrowserMode, ResolveInfo?> {
        val browsers = BrowserResolver.queryBrowsers(context)
        addAllBrowsers(browsers, currentResolveList)

        val mode = preferenceRepository.getString(
            PreferenceRepository.browserMode,
            BrowserMode.persister,
            BrowserMode.reader
        )!!
        val selectedBrowser = preferenceRepository.getString(PreferenceRepository.selectedBrowser)

        when (mode) {
            is BrowserMode.None -> removeBrowsers(browsers, currentResolveList)
            is BrowserMode.SelectedBrowser -> {
                val found = browsers.find {
                    it.activityInfo.packageName == selectedBrowser
                }

                if (found != null) {
                    removeBrowsers(browsers, currentResolveList)
                    currentResolveList.add(found)

                    return mode to found
                }
            }
            else -> {}
        }

        return mode to null
    }

    private fun removeBrowsers(
        browsers: List<ResolveInfo>,
        currentResolveList: MutableList<ResolveInfo>
    ) {
        val toRemove = currentResolveList.filter { resolve ->
            browsers.find { browser ->
                resolve.activityInfo.isEqualTo(browser.activityInfo)
            } != null
        }
        currentResolveList.removeAll(toRemove)
    }

    private fun addAllBrowsers(
        browsers: List<ResolveInfo>,
        currentResolveList: MutableList<ResolveInfo>
    ) {
        val initialList = ArrayList(currentResolveList)

        browsers.forEach { browser ->
            val notFound = initialList.find {
                it.activityInfo.isEqualTo(browser.activityInfo)
            } == null
            if (notFound) {
                currentResolveList.add(browser)
            }
        }
    }
}
