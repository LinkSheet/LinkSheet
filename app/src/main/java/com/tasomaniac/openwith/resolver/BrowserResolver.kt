package com.tasomaniac.openwith.resolver

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import fe.linksheet.BuildConfig
import fe.linksheet.extension.toDisplayActivityInfo

object BrowserResolver {

    fun resolve(context: Context) = queryBrowsers(context).map {
        it.toDisplayActivityInfo(context)
    }

    fun queryBrowsers(context: Context): Set<ResolveInfo> {
        val browserIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.parse("http:"))

        val resolvedBrowsers =
            context.packageManager.queryIntentActivities(browserIntent, PackageManager.MATCH_ALL)
        resolvedBrowsers.removeAll { it.activityInfo.packageName == BuildConfig.APPLICATION_ID }

        return resolvedBrowsers.distinctBy { it.activityInfo.packageName }.toSet()
    }
}
