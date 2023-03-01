package com.tasomaniac.openwith.resolver

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.tasomaniac.openwith.extension.componentName
import com.tasomaniac.openwith.extension.isHttp
import com.tasomaniac.openwith.preferred.PreferredResolver
import fe.linksheet.BuildConfig
import fe.linksheet.extension.getUri
import fe.linksheet.extension.sourceIntent
import fe.linksheet.resolver.ResolveListGrouper

object ResolveIntents {

    fun resolve(context: Context, intent: Intent): IntentResolverResult {
        val uri = intent.getUri()

        Log.d("ResolveIntents", "Intent data: $uri, intent: $intent")

        val preferredApp = uri?.let {
            PreferredResolver.resolve(context, it.host!!)
        }

        Log.d("ResolveIntents", "PreferredApp: $preferredApp")

        val hostHistory = uri?.let {
            PreferredResolver.resolveHostHistory(context, it.host!!)
        } ?: emptyMap()


        Log.d("ResolveIntents", "HostHistory: $hostHistory")


        val alwaysPreferred = preferredApp?.app?.alwaysPreferred

        val sourceIntent = intent.sourceIntent()
        Log.d("ResolveIntents", "$sourceIntent")

        val resolveListPreSort = context.packageManager.queryIntentActivities(
            sourceIntent, PackageManager.MATCH_ALL
        )

        resolveListPreSort.removeAll {
            it.activityInfo.packageName == BuildConfig.APPLICATION_ID
        }

        Log.d("ResolveIntents", "PreferredApp ComponentName: ${preferredApp?.app?.componentName}")

        val browserMode = if (sourceIntent.isHttp()) {
            BrowserHandler.handleBrowsers(context, resolveListPreSort)
        } else null


        val (resolved, filteredItem, showExtended) = ResolveListGrouper.resolveList(
            context,
            resolveListPreSort,
            hostHistory,
            preferredApp?.app?.componentName
        )

        val selectedBrowserIsSingleOption =
            browserMode?.first == BrowserHandler.BrowserMode.SelectedBrowser
                    && resolveListPreSort.singleOrNull()?.activityInfo?.componentName() == browserMode.second?.activityInfo?.componentName()

        val noBrowsersPresentOnlySingleApp =
            browserMode?.first == BrowserHandler.BrowserMode.None && resolveListPreSort.size == 1


        Log.d(
            "ResolveIntents",
            "Resolved: $resolved, filteredItem: $filteredItem, showExtended: $showExtended, selectedBrowserIsSingleOption: $selectedBrowserIsSingleOption"
        )

        return IntentResolverResult(
            resolved,
            filteredItem,
            showExtended,
            alwaysPreferred,
            selectedBrowserIsSingleOption || noBrowsersPresentOnlySingleApp
        )
    }
}