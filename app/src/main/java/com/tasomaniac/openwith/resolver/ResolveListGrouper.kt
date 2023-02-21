package com.tasomaniac.openwith.resolver

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.util.Log
import androidx.core.content.ContextCompat
import java.util.*
import java.util.concurrent.TimeUnit

object ResolveListGrouper {
    private var filteredItem: DisplayActivityInfo? = null
    private var showExtended = false

    /**
     * Taken from AOSP, don't try to understand what's going on.
     */
    fun groupResolveList(
        context: Context,
        current: List<ResolveInfo>,
        sourceIntent: Intent,
        lastChosenComponent: ComponentName?
    ): Triple<List<DisplayActivityInfo>, DisplayActivityInfo?, Boolean> {
        current.sortedWith(ResolverComparator(context.packageManager, usageStatsFrom(context), sourceIntent))

        val grouped: MutableList<DisplayActivityInfo> = ArrayList()

        // Check for applications with same name and use application name or
        // package name if necessary
        var r0 = current[0]
        var start = 0
        var r0Label = r0.loadLabel(context.packageManager)
        val size = current.size
        for (i in 1 until size) {
            if (r0Label == null) {
                r0Label = r0.activityInfo.packageName
            }
            val ri = current[i]
            var riLabel = ri.loadLabel(context.packageManager)
            if (riLabel == null) {
                riLabel = ri.activityInfo.packageName
            }
            if (riLabel == r0Label) {
                continue
            }
            processGroup(
                context,
                grouped,
                current,
                start,
                i - 1,
                r0,
                r0Label,
                lastChosenComponent,
            )


            r0 = ri
            r0Label = riLabel
            start = i
        }
        // Process last group
        processGroup(
            context,
            grouped,
            current,
            start,
            size - 1,
            r0,
            r0Label,
            lastChosenComponent,
        )

        return Triple(grouped, filteredItem, showExtended)
    }

    /**
     * Taken from AOSP, don't try to understand what's going on.
     */
    private fun processGroup(
        context: Context,
        grouped: MutableList<DisplayActivityInfo>,
        current: List<ResolveInfo>,
        start: Int,
        end: Int,
        ro: ResolveInfo,
        displayLabel: CharSequence?,
        lastChosenComponent: ComponentName?
    ): Pair<DisplayActivityInfo?, Boolean> {
        // Process labels from start to i
        val num = end - start + 1
        if (num == 1) {
            // No duplicate labels. Use label for entry at start
            val activityInfo = DisplayActivityInfo(
                ro.activityInfo,
                displayLabel!!.toString(), null
            )
            activityInfo.displayIcon = IconLoader.loadFor(context, ro.activityInfo)
            if (isLastChosenPosition(ro.activityInfo, lastChosenComponent)) {
                filteredItem = activityInfo
            } else {
                grouped.add(activityInfo)
            }
        } else {
            showExtended = true
            var usePkg = false
            val startApp = ro.activityInfo.applicationInfo.loadLabel(
                context.packageManager
            )

            // Use HashSet to track duplicates
            val duplicates = HashSet<CharSequence?>()
            duplicates.add(startApp)
            for (j in start + 1..end) {
                val jRi = current[j]
                val jApp = jRi.activityInfo.applicationInfo.loadLabel(
                    context.packageManager
                )
                if (duplicates.contains(jApp)) {
                    usePkg = true
                    break
                } else {
                    duplicates.add(jApp)
                }
            }
            // Clear HashSet for later use
            duplicates.clear()

            for (k in start..end) {
                val add = current[k].activityInfo
                val activityInfo = displayResolveInfoToAdd(context, usePkg, add, displayLabel)
                activityInfo.displayIcon = IconLoader.loadFor(context, add)
                if (isLastChosenPosition(add, lastChosenComponent)) {
                    filteredItem = activityInfo
                } else {
                    grouped.add(activityInfo)
                }
            }
        }

        return filteredItem to showExtended
    }

    private fun displayResolveInfoToAdd(
        context: Context,
        usePackageName: Boolean,
        activityInfo: ActivityInfo,
        displayLabel: CharSequence?
    ): DisplayActivityInfo {
        return if (usePackageName) {
            // Use package name for all entries from start to end-1
            DisplayActivityInfo(activityInfo, displayLabel!!.toString(), activityInfo.packageName)
        } else {
            // Use application name for all entries from start to end-1
            val extendedLabel = activityInfo.applicationInfo.loadLabel(
                context.packageManager
            )
            DisplayActivityInfo(activityInfo, displayLabel!!.toString(), extendedLabel)
        }
    }

    private fun isLastChosenPosition(
        activityInfo: ActivityInfo,
        lastChosenComponent: ComponentName?
    ): Boolean {
        Log.d(
            "IsLastChosenPosition",
            "lastChosenComponent: $lastChosenComponent, activityInfo: $activityInfo"
        )
        return lastChosenComponent != null && lastChosenComponent.packageName == activityInfo.packageName && lastChosenComponent.className == activityInfo.name
    }

    private val USAGE_STATS_PERIOD = TimeUnit.DAYS.toMillis(14)

    private fun usageStatsFrom(context: Context): Map<String?, UsageStats?>? {
        val usageStatsManager = ContextCompat.getSystemService(
            context,
            UsageStatsManager::class.java
        )

        val sinceTime: Long = System.currentTimeMillis() - USAGE_STATS_PERIOD
        return usageStatsManager?.queryAndAggregateUsageStats(sinceTime, System.currentTimeMillis())
    }
}