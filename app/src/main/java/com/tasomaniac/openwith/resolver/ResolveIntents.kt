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
import fe.linksheet.data.AppSelectionHistory
import fe.linksheet.extension.getUri
import fe.linksheet.extension.sourceIntent
import fe.linksheet.extension.toDisplayActivityInfo
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

        val currentResolveList = context.packageManager.queryIntentActivities(
            sourceIntent, PackageManager.MATCH_ALL
        )

        currentResolveList.removeAll {
            it.activityInfo.packageName == BuildConfig.APPLICATION_ID
        }

        Log.d("ResolveIntents", "PreferredApp ComponentName: ${preferredApp?.app?.componentName}")

        if (sourceIntent.isHttp()) {
            BrowserHandler.handleBrowsers(context, currentResolveList)
        }

        val (resolved, filteredItem, showExtended) = groupResolveList(
            context,
            currentResolveList,
            hostHistory,
            sourceIntent,
            preferredApp?.app?.componentName
        )

        Log.d(
            "ResolveIntents",
            "Resolved: $resolved, filteredItem: $filteredItem, showExtended: $showExtended"
        )

        return IntentResolverResult(
            resolved,
            filteredItem,
            showExtended,
            alwaysPreferred,
        )
    }

    private fun groupResolveList(
        context: Context,
        currentResolveList: List<ResolveInfo>,
        historyMap: Map<String, AppSelectionHistory>,
        sourceIntent: Intent,
        lastChosenComponent: ComponentName?
    ): Triple<List<DisplayActivityInfo>, DisplayActivityInfo?, Boolean> {
        return if (currentResolveList.isEmpty()) {
            Triple(emptyList(), null, false)
        } else ResolveListGrouper.resolveList(
            context,
            currentResolveList,
            historyMap,
            lastChosenComponent
        )
    }
}