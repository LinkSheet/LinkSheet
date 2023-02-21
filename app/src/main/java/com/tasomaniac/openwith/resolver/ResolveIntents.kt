package com.tasomaniac.openwith.resolver

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.UserManager
import android.util.Log
import com.tasomaniac.openwith.preferred.PreferredResolver
import fe.linksheet.BuildConfig
import fe.linksheet.util.sourceIntent
import org.lsposed.hiddenapibypass.HiddenApiBypass

object ResolveIntents {

    fun resolve(context: Context, intent: Intent): IntentResolverResult {
        Log.d("ResolveIntents", "Intent data: ${intent.data}")

        val preferredApp = intent.data?.let {
            PreferredResolver.resolve(context, it)
        }

        Log.d("ResolveIntents", "PreferredApp: $preferredApp")


        val alwaysPreferred = preferredApp?.app?.alwaysPreferred

        val sourceIntent = intent.sourceIntent()
        Log.d("ResolveIntents", "$sourceIntent")

        val currentResolveList = context.packageManager.queryIntentActivities(sourceIntent, PackageManager.MATCH_ALL)

        currentResolveList.removeAll {
            it.activityInfo.packageName == BuildConfig.APPLICATION_ID
        }

        Log.d("ResolveIntents", "PreferredApp ComponentName: ${preferredApp?.app?.componentName}")

//        if (sourceIntent.isHttp()) {
//            browserHandlerFactory.create(currentResolveList).handleBrowsers()
//        }

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
            alwaysPreferred
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
        } else ResolveListGrouper.groupResolveList(context, currentResolveList, sourceIntent, lastChosenComponent)
    }
}