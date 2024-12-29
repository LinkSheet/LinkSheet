package fe.linksheet.module.resolver.util

import android.app.usage.UsageStats
import android.content.pm.ResolveInfo
import fe.linksheet.module.resolver.FilteredBrowserList
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.resolver.DisplayActivityInfo
import java.util.concurrent.TimeUnit
import kotlin.collections.remove

class AppSorter(
    private val queryAndAggregateUsageStats: (beginTime: Long, endTime: Long) -> Map<String, UsageStats>,
    private val toDisplayActivityInfo: (ResolveInfo, browser: Boolean) -> DisplayActivityInfo,
) {
    private val emptyComparator: Comparator<DisplayActivityInfo> = Comparator { _, _ -> 0 }
    private val usageStatsPeriod = TimeUnit.DAYS.toMillis(14)

    fun sort(
        appList: FilteredBrowserList,
        lastChosen: PreferredApp?,
        historyMap: Map<String, Long>,
        returnLastChosen: Boolean = true,
    ): Pair<List<DisplayActivityInfo>, DisplayActivityInfo?> {
        val infos = toDisplay(appList.apps, appList.browsers)
        val filtered = if (returnLastChosen && lastChosen != null) infos.remove(lastChosen.pkg) else null

        val comparator = listOfNotNull(
            createHistoryComparator(historyMap),
            createUsageStatComparator(),
            DisplayActivityInfo.Companion.labelComparator
        ).fold(emptyComparator) { current, next -> current.then(next) }

        val sorted = infos.values.sortedWith(comparator)
        return sorted to filtered
    }

    private fun createHistoryComparator(historyMap: Map<String, Long>?): Comparator<DisplayActivityInfo>? {
        if (historyMap == null) return null
        return compareByDescending { app -> historyMap[app.packageName] ?: -1L }
    }

    private fun createUsageStatComparator(): Comparator<DisplayActivityInfo> {
        val now = System.currentTimeMillis()
        val sinceTime = now - usageStatsPeriod
        val usageStatsMap = queryAndAggregateUsageStats(sinceTime, now)

        return compareByDescending { app -> usageStatsMap[app.packageName]?.totalTimeInForeground ?: -1L }
    }

    private fun toDisplay(
        apps: List<ResolveInfo>,
        browsers: List<ResolveInfo>,
    ): MutableMap<String, DisplayActivityInfo> {
        val map = mutableMapOf<String, DisplayActivityInfo>()

        for (app in apps) {
            val info = toDisplayActivityInfo(app, false)
            map[info.packageName] = info
        }

        for (browser in browsers) {
            val info = toDisplayActivityInfo(browser, true)
            map[info.packageName] = info
        }

        return map
    }
}
