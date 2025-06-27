package fe.linksheet.module.app

import fe.linksheet.extension.android.toImageBitmap
import fe.linksheet.feature.app.ActivityAppInfo
import fe.linksheet.feature.app.ActivityAppInfoStatus
import fe.linksheet.feature.app.AppInfo
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.resolver.DisplayActivityInfo
import fe.linksheet.util.RefactorGlue

@RefactorGlue(reason = "Ensure compatibility of (new) PackageService with old UI")
object ActivityAppInfoSortGlue {
    private val valueAndLabelComparator = compareByDescending<ActivityAppInfoStatus> { (_, status) ->
        status
    }.thenBy { (activityInfo, _) -> activityInfo.compareLabel }

    private fun mapBrowserState(appInfo: ActivityAppInfo, pkgs: Set<String>): ActivityAppInfoStatus {
        return appInfo to (appInfo.packageName in pkgs)
    }

    fun mapBrowserState(browsers: List<ActivityAppInfo>, pkgs: Set<String>): Map<ActivityAppInfo, Boolean> {
        return browsers.map { mapBrowserState(it, pkgs) }.sortedWith(valueAndLabelComparator).toMap()
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
