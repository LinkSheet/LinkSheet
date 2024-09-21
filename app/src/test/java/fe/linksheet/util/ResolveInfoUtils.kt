package fe.linksheet.util

import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo

fun buildResolveInfoTestMock(block: ResolveInfo.(ActivityInfo, ApplicationInfo) -> Unit): ResolveInfo {
    val resolveInfo = ResolveInfo()
    val activityInfo = ActivityInfo()
    val applicationInfo = ApplicationInfo()

    activityInfo.applicationInfo = applicationInfo
    resolveInfo.activityInfo = activityInfo

    block(resolveInfo, activityInfo, applicationInfo)

    applicationInfo.packageName = activityInfo.packageName
    return resolveInfo
}
