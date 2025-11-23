package fe.linksheet.feature.app

import app.linksheet.feature.app.ActivityAppInfo
import app.linksheet.feature.app.ActivityAppInfoStatus
import app.linksheet.feature.app.AppInfo
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.repository.whitelisted.WhitelistedBrowserInfo
import fe.linksheet.module.resolver.DisplayActivityInfo
import fe.linksheet.util.RefactorGlue

@RefactorGlue(reason = "Ensure compatibility of (new) PackageService with old UI")
object ActivityAppInfoSortGlue {
    private val valueAndLabelComparator = compareByDescending<ActivityAppInfoStatus> { (_, status) ->
        status
    }.thenBy { (activityInfo, _) -> activityInfo.compareLabel }

    private fun mapBrowserState(appInfo: ActivityAppInfo, helper: WhitelistedBrowserInfo): ActivityAppInfoStatus {
        val flag = (appInfo.packageName in helper.packageNames) || (appInfo.componentName in helper.componentNames)
        return appInfo to flag
    }

    fun mapBrowserState(browsers: List<ActivityAppInfo>, info: WhitelistedBrowserInfo): Map<ActivityAppInfo, Boolean> {
        return browsers.map { mapBrowserState(it, info) }.sortedWith(valueAndLabelComparator).toMap()
    }
}

@RefactorGlue(reason = "Ensure compatibility of (new) PackageService with old UI")
object ActivityAppInfoGlue {
    fun toDisplayActivityInfo(appInfo: ActivityAppInfo): DisplayActivityInfo {
        return DisplayActivityInfo(
            componentInfo = appInfo.componentInfo,
            label = appInfo.label,
            browser = false,
            icon = appInfo.icon!!
        )
    }
}

fun ActivityAppInfo.toPreferredApp(host: String, alwaysPreferred: Boolean): PreferredApp {
    return PreferredApp.new(
        host = host,
        pkg = packageName,
        cmp = componentName,
        always = alwaysPreferred
    )
}

fun AppInfo.toPreferredApp(host: String, alwaysPreferred: Boolean): PreferredApp {
    return PreferredApp.new(
        host = host,
        pkg = packageName,
        cmp = null,
        always = alwaysPreferred
    )
}
