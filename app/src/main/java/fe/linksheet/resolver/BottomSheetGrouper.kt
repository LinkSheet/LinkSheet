package fe.linksheet.resolver

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.extension.findIndexed
import fe.linksheet.extension.toDisplayActivityInfo
import java.util.concurrent.TimeUnit

object BottomSheetGrouper {
    fun group(
        context: Context,
        current: List<ResolveInfo>,
        historyMap: Map<String, Long>?,
        lastChosenPreferredApp: PreferredApp?,
        returnFilteredItem: Boolean = true,
    ): Triple<List<DisplayActivityInfo>, DisplayActivityInfo?, Boolean> {
        val grouped = current.toMutableList()

        val filteredPair = grouped.findIndexed {
            isLastChosenPosition(it.activityInfo, lastChosenPreferredApp?.componentName)
        }

        val filteredItem = if (filteredPair != null && (returnFilteredItem || lastChosenPreferredApp?.alwaysPreferred == true)) {
            grouped.removeAt(filteredPair.second)
            filteredPair.first.toDisplayActivityInfo(context)
        } else null

        var comparator = if (historyMap != null) {
            compareByDescending<DisplayActivityInfo> { app ->
                historyMap[app.activityInfo.packageName] ?: -1L
            }
        } else Comparator { _, _ -> 0 }

        val usageStatsMap = usageStatsFrom(context)
        if (usageStatsMap != null) {
            comparator = comparator.thenByDescending { app ->
                usageStatsMap[app.packageName]?.totalTimeInForeground ?: -1L
            }
        }

        comparator = comparator.thenBy { app -> app.label.lowercase() }

        val displayLabels = mutableSetOf<String>()
        var showExtended = false

        val returnGrouped = grouped.map { app ->
            app.toDisplayActivityInfo(context).also {
                if (!displayLabels.add(it.label)) {
                    showExtended = true
                }
            }
        }.sortedWith(comparator)

        return Triple(returnGrouped, filteredItem, showExtended)
    }


    private fun isLastChosenPosition(
        activityInfo: ActivityInfo,
        lastChosenComponent: ComponentName?
    ) = lastChosenComponent != null && lastChosenComponent.packageName == activityInfo.packageName && lastChosenComponent.className == activityInfo.name


    private val usageStatsPeriod = TimeUnit.DAYS.toMillis(14)

    private fun usageStatsFrom(context: Context): Map<String?, UsageStats?>? {
        val usageStatsManager = context.getSystemService(UsageStatsManager::class.java)

        val sinceTime: Long = System.currentTimeMillis() - usageStatsPeriod
        return usageStatsManager?.queryAndAggregateUsageStats(sinceTime, System.currentTimeMillis())
    }
}