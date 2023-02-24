package com.tasomaniac.openwith.resolver

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log
import com.tasomaniac.openwith.extension.componentName
import com.tasomaniac.openwith.extension.isHttp
import com.tasomaniac.openwith.preferred.PreferredResolver
import fe.linksheet.BuildConfig
import fe.linksheet.extension.getUri
import fe.linksheet.extension.sourceIntent

object ResolveIntents {

    fun resolve(context: Context, intent: Intent): IntentResolverResult {
        val uri = intent.getUri()

        Log.d("ResolveIntents", "Intent data: $uri, intent: $intent")

        val preferredApp = uri?.let {
            PreferredResolver.resolve(context, it)
        }

        Log.d("ResolveIntents", "PreferredApp: $preferredApp")


        val alwaysPreferred = preferredApp?.app?.alwaysPreferred

        val sourceIntent = intent.sourceIntent()
        Log.d("ResolveIntents", "$sourceIntent")

        val currentResolveList = context.packageManager.queryIntentActivities(
            sourceIntent, PackageManager.MATCH_ALL
        )

        currentResolveList.removeAll {
            it.activityInfo.packageName == BuildConfig.APPLICATION_ID
        }

        Log.d("ResolveIntents", "PreferredApp ComponentName: ${preferredApp?.app?.componentName}")

        val browserMode = if (sourceIntent.isHttp()) {
            BrowserHandler.handleBrowsers(context, currentResolveList)
        } else null

        val singleBrowserOnlyResolvedItem =
            browserMode != null && browserMode.first == BrowserHandler.BrowserMode.SelectedBrowser
                    && currentResolveList.size == 1
                    && currentResolveList.first().activityInfo.componentName() == browserMode.second?.activityInfo?.componentName()

        val (resolved, filteredItem, showExtended) = groupResolveList(
            context,
            currentResolveList,
            sourceIntent,
            preferredApp?.app?.componentName
        )

        Log.d(
            "ResolveIntents",
            "Resolved: $resolved, filteredItem: $filteredItem, showExtended: $showExtended"
        )

        return IntentResolverResult(
            if (filteredItem != null) resolved.toMutableList()
                .apply { remove(filteredItem) } else resolved,
            filteredItem,
            showExtended,
            alwaysPreferred,
            singleBrowserOnlyResolvedItem
        )
    }

    private fun groupResolveList(
        context: Context,
        currentResolveList: List<ResolveInfo>,
        sourceIntent: Intent,
        lastChosenComponent: ComponentName?
    ): Triple<List<DisplayActivityInfo>, DisplayActivityInfo?, Boolean> {
        return if (currentResolveList.isEmpty()) {
            Triple(emptyList(), null, false)
        } else ResolveListGrouper.groupResolveList(
            context,
            currentResolveList,
            sourceIntent,
            lastChosenComponent
        )
    }
}