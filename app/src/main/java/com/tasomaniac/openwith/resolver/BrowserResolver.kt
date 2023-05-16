package com.tasomaniac.openwith.resolver

import android.content.Context
import android.content.pm.ResolveInfo
import fe.linksheet.extension.allBrowsersIntent
import fe.linksheet.extension.queryResolveInfosByIntent
import fe.linksheet.extension.toDisplayActivityInfo
import fe.linksheet.extension.toPackageKeyedMap
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object BrowserResolver : KoinComponent {
    private val context by inject<Context>()

    fun queryPackageKeyedBrowsers() = queryBrowsers().toPackageKeyedMap()
    fun queryDisplayActivityInfoBrowsers(sorted: Boolean) = queryBrowsers()
        .toDisplayActivityInfo(context, sorted)

    fun queryBrowsers() = context.packageManager.queryResolveInfosByIntent(
        allBrowsersIntent, true
    )
}
