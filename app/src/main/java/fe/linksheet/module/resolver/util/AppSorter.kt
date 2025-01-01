package fe.linksheet.module.resolver.util

import android.app.usage.UsageStats
import android.content.pm.ResolveInfo
import fe.linksheet.module.app.ActivityAppInfo
import fe.linksheet.module.resolver.FilteredBrowserList
import fe.linksheet.module.database.entity.PreferredApp
import java.util.concurrent.TimeUnit
import kotlin.collections.remove

class AppSorter(
    private val queryAndAggregateUsageStats: (beginTime: Long, endTime: Long) -> Map<String, UsageStats>,
    private val toAppInfo: (ResolveInfo, browser: Boolean) -> ActivityAppInfo,
) {
    private val emptyComparator: Comparator<ActivityAppInfo> = Comparator { _, _ -> 0 }
    private val usageStatsPeriod = TimeUnit.DAYS.toMillis(14)

    fun sort(
        appList: FilteredBrowserList,
        lastChosen: PreferredApp?,
        historyMap: Map<String, Long>,
        returnLastChosen: Boolean = true,
    ): Pair<List<ActivityAppInfo>, ActivityAppInfo?> {
        val infos = toDisplay(appList.apps, appList.browsers)
        val filtered = if (returnLastChosen && lastChosen != null) infos.remove(lastChosen.pkg) else null

        val comparator = listOfNotNull(
            createHistoryComparator(historyMap),
            createUsageStatComparator(),
            ActivityAppInfo.labelComparator
        ).fold(emptyComparator) { current, next -> current.then(next) }

        val sorted = infos.values.sortedWith(comparator)
        return sorted to filtered
    }

    private fun createHistoryComparator(historyMap: Map<String, Long>?): Comparator<ActivityAppInfo>? {
        if (historyMap == null) return null
        return compareByDescending { app -> historyMap[app.packageName] ?: -1L }
    }

    private fun createUsageStatComparator(): Comparator<ActivityAppInfo> {
        val now = System.currentTimeMillis()
        val sinceTime = now - usageStatsPeriod
        val usageStatsMap = queryAndAggregateUsageStats(sinceTime, now)

        return compareByDescending { app -> usageStatsMap[app.packageName]?.totalTimeInForeground ?: -1L }
    }

    private fun toDisplay(
        apps: List<ResolveInfo>,
        browsers: List<ResolveInfo>,
    ): MutableMap<String, ActivityAppInfo> {
        val map = mutableMapOf<String, ActivityAppInfo>()

        for (app in apps) {
            val info = toAppInfo(app, false)
            map[info.packageName] = info
        }

        for (browser in browsers) {
            val info = toAppInfo(browser, true)
            map[info.packageName] = info
        }

        return map
    }
}
