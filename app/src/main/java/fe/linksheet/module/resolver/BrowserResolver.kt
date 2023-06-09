package fe.linksheet.module.resolver

import android.app.Application
import fe.linksheet.extension.android.allBrowsersIntent
import fe.linksheet.extension.android.queryResolveInfosByIntent
import fe.linksheet.extension.android.toDisplayActivityInfos
import fe.linksheet.extension.android.toPackageKeyedMap

class BrowserResolver(val context: Application) {
    fun queryPackageKeyedBrowsers() = queryBrowsers().toPackageKeyedMap()
    fun queryDisplayActivityInfoBrowsers(sorted: Boolean) = queryBrowsers()
        .toDisplayActivityInfos(context, sorted)

    fun queryBrowsers() = context.packageManager.queryResolveInfosByIntent(
        allBrowsersIntent, true
    )
}
