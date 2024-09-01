package fe.linksheet.extension.android

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import fe.linksheet.resolver.DisplayActivityInfo

fun ResolveInfo.toDisplayActivityInfo(context: Context, browser: Boolean = false): DisplayActivityInfo {
    return toDisplayActivityInfo(context.packageManager, browser)
}

fun ResolveInfo.toDisplayActivityInfo(packageManager: PackageManager, browser: Boolean = false): DisplayActivityInfo {
    return DisplayActivityInfo(
        resolvedInfo = this,
        label = loadLabel(packageManager).toString(),
        browser = browser
    )
}

fun Iterable<ResolveInfo>.toDisplayActivityInfos(
    context: Context,
    sorted: Boolean = true,
    browser: Boolean = false,
): List<DisplayActivityInfo> {
    return map { it.toDisplayActivityInfo(context, browser) }.labelSorted(sorted)
}

fun Map<String, ResolveInfo>.toDisplayActivityInfos(
    packageManager: PackageManager,
    sorted: Boolean = true,
    browser: Boolean = false,
): List<DisplayActivityInfo> {
    return map { (_, it) -> it.toDisplayActivityInfo(packageManager, browser) }.labelSorted(sorted)
}

fun Iterable<ResolveInfo>.toPackageKeyedMap() = associateBy { it.activityInfo.packageName }
