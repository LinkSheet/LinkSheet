package fe.linksheet.experiment.improved.resolver

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ResolveInfo
import fe.linksheet.extension.android.toDisplayActivityInfo
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.resolver.BrowserHandler
import fe.linksheet.resolver.DisplayActivityInfo
import java.util.concurrent.TimeUnit

object AppSorter {
    private val emptyComparator: Comparator<DisplayActivityInfo> = Comparator { _, _ -> 0 }
    private val usageStatsPeriod = TimeUnit.DAYS.toMillis(14)

    fun sort(
        context: Context,
        appList: BrowserHandler.FilteredBrowserList,
        lastChosen: PreferredApp?,
        historyMap: Map<String, Long>,
        returnLastChosen: Boolean = true,
    ): Pair<List<DisplayActivityInfo>, DisplayActivityInfo?> {
        val infos = toDisplay(context, appList.apps, appList.browsers)
        val filtered = if (returnLastChosen && lastChosen != null) infos[lastChosen.pkg] else null

        val comparator = listOfNotNull(
            createHistoryComparator(historyMap),
            createUsageStatComparator(context),
            DisplayActivityInfo.labelComparator
        ).fold(emptyComparator) { current, next -> current.then(next) }

        val sorted = infos.values.sortedWith(comparator)
        return sorted to filtered
    }

    private fun createHistoryComparator(historyMap: Map<String, Long>?): Comparator<DisplayActivityInfo>? {
        if (historyMap == null) return null
        return compareByDescending { app -> historyMap[app.packageName] ?: -1L }
    }

    private fun createUsageStatComparator(context: Context): Comparator<DisplayActivityInfo>? {
        val usageStatsManager = context.getSystemService(UsageStatsManager::class.java)

        val sinceTime: Long = System.currentTimeMillis() - usageStatsPeriod
        val usageStatsMap = usageStatsManager?.queryAndAggregateUsageStats(
            sinceTime,
            System.currentTimeMillis()
        ) ?: return null

        return compareByDescending { app -> usageStatsMap[app.packageName]?.totalTimeInForeground ?: -1L }
    }

    private fun toDisplay(
        context: Context,
        apps: List<ResolveInfo>,
        browsers: List<ResolveInfo>,
    ): Map<String, DisplayActivityInfo> {
        val map = mutableMapOf<String, DisplayActivityInfo>()

        for (app in apps) {
            val info = app.toDisplayActivityInfo(context, false)
            map[info.packageName] = info
        }

        for (browser in browsers) {
            val info = browser.toDisplayActivityInfo(context, true)
            map[info.packageName] = info
        }

        return map
    }
}
