package fe.linksheet.resolver

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import fe.kotlin.extension.iterable.findWithIndexOrNull
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.extension.android.toDisplayActivityInfo
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

        val filteredPair = grouped.findWithIndexOrNull {
            isLastChosenPosition(it.activityInfo, lastChosenPreferredApp)
        }

        val filteredPairs = grouped.mapIndexedNotNull { index, resolveInfo ->
            (resolveInfo to index).takeIf { resolveInfo.activityInfo.packageName == lastChosenPreferredApp?.pkg }
        }

        val filteredItem = if (filteredPair != null && (returnFilteredItem || lastChosenPreferredApp?.alwaysPreferred == true)) {
            grouped.removeAt(filteredPair.second)
            filteredPair.first.toDisplayActivityInfo(context)
        } else if (filteredPairs.size == 1 && (returnFilteredItem || lastChosenPreferredApp?.alwaysPreferred == true)) {
            val first = filteredPairs.first()
            first.second.let { grouped.removeAt(it) }
            first.first.toDisplayActivityInfo(context)
        } else {
            null
        }

        var comparator = if (historyMap != null) {
            compareByDescending<DisplayActivityInfo> { app ->
                historyMap[app.packageName] ?: -1L
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
        lastChosenComponent: PreferredApp?
    ) = lastChosenComponent != null && lastChosenComponent.pkg == activityInfo.packageName


    private val usageStatsPeriod = TimeUnit.DAYS.toMillis(14)

    private fun usageStatsFrom(context: Context): Map<String?, UsageStats?>? {
        val usageStatsManager = context.getSystemService(UsageStatsManager::class.java)

        val sinceTime: Long = System.currentTimeMillis() - usageStatsPeriod
        return usageStatsManager?.queryAndAggregateUsageStats(sinceTime, System.currentTimeMillis())
    }
}
