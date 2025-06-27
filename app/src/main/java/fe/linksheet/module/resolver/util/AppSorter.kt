package fe.linksheet.module.resolver.util

import android.app.usage.UsageStats
import android.content.pm.ResolveInfo
import fe.linksheet.extension.android.activityDescriptor
import fe.linksheet.feature.app.ActivityAppInfo
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.resolver.FilteredBrowserList
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class AppSorter(
    private val queryAndAggregateUsageStats: (beginTime: Long, endTime: Long) -> Map<String, UsageStats>,
    private val toAppInfo: (ResolveInfo, browser: Boolean) -> ActivityAppInfo,
    private val clock: Clock,
    private val usageStatsPeriod: Duration = 14.days,
) {
    private val emptyComparator: Comparator<ActivityAppInfo> = Comparator { _, _ -> 0 }

    fun sort(
        appList: FilteredBrowserList,
        lastChosen: PreferredApp?,
        historyMap: Map<String, Long>,
        returnLastChosen: Boolean = true,
    ): Pair<List<ActivityAppInfo>, ActivityAppInfo?> {
        val infos = toDisplay(appList.apps, appList.browsers)
        val filtered = when {
            lastChosen?.alwaysPreferred == true || returnLastChosen -> maybeGetAndRemoveLastChosen(infos, lastChosen)
            else -> null
        }

        val comparator = listOfNotNull(
            createHistoryComparator(historyMap),
            createUsageStatComparator(),
            ActivityAppInfo.labelComparator
        ).fold(emptyComparator) { current, next -> current.then(next) }

        val sorted = infos.values.sortedWith(comparator)
        return sorted to filtered
    }

    private fun maybeGetAndRemoveLastChosen(
        infos: MutableMap<String, ActivityAppInfo>,
        lastChosen: PreferredApp?,
    ): ActivityAppInfo? {
        if (lastChosen == null) return null

        val lastChosenEntry = infos.entries
            .firstOrNull { it.value.componentName == lastChosen.cmp }

        return infos.remove(lastChosenEntry?.key)
    }

    private fun createHistoryComparator(historyMap: Map<String, Long>?): Comparator<ActivityAppInfo>? {
        if (historyMap == null) return null
        return compareByDescending { app -> historyMap[app.packageName] ?: -1L }
    }

    @OptIn(ExperimentalTime::class)
    private fun createUsageStatComparator(): Comparator<ActivityAppInfo> {
        val now = clock.now()
        val sinceTime = now - usageStatsPeriod
        val usageStatsMap = queryAndAggregateUsageStats(sinceTime.toEpochMilliseconds(), now.toEpochMilliseconds())

        return compareByDescending { app -> usageStatsMap[app.packageName]?.totalTimeInForeground ?: -1L }
    }

    private fun toDisplay(
        apps: List<ResolveInfo>,
        browsers: List<ResolveInfo>,
    ): MutableMap<String, ActivityAppInfo> {
        val map = mutableMapOf<String, ActivityAppInfo>()

        for (app in apps) {
            val info = toAppInfo(app, false)
            map[app.activityInfo.activityDescriptor] = info
        }

        for (browser in browsers) {
            val info = toAppInfo(browser, true)
            map[browser.activityInfo.activityDescriptor] = info
        }

        return map
    }
}
