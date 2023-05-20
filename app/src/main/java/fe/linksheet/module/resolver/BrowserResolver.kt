package fe.linksheet.module.resolver

import android.app.Application
import android.content.Context
import fe.linksheet.extension.allBrowsersIntent
import fe.linksheet.extension.queryResolveInfosByIntent
import fe.linksheet.extension.toDisplayActivityInfos
import fe.linksheet.extension.toPackageKeyedMap
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BrowserResolver(val context: Application) {
    fun queryPackageKeyedBrowsers() = queryBrowsers().toPackageKeyedMap()
    fun queryDisplayActivityInfoBrowsers(sorted: Boolean) = queryBrowsers()
        .toDisplayActivityInfos(context, sorted)

    fun queryBrowsers() = context.packageManager.queryResolveInfosByIntent(
        allBrowsersIntent, true
    )
}
