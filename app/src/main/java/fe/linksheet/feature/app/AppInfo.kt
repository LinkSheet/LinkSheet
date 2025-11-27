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
        var flag = (appInfo.componentName in helper.componentNames)
        var isSourcePackageNameOnly = false
        if (appInfo.packageName in helper.packageNames) {
            isSourcePackageNameOnly = true
            flag = true
        }
        return ActivityAppInfoStatus(appInfo, flag, isSourcePackageNameOnly)
    }

    fun mapBrowserState(browsers: List<ActivityAppInfo>, info: WhitelistedBrowserInfo): List<ActivityAppInfoStatus> {
        return browsers.map { mapBrowserState(it, info) }.sortedWith(valueAndLabelComparator)
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
