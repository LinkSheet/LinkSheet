package fe.linksheet.extension.android

import android.content.Context
import android.content.pm.ResolveInfo
import fe.linksheet.module.resolver.UriViewActivity
import fe.linksheet.resolver.DisplayActivityInfo

fun UriViewActivity.toDisplayActivityInfo(context: Context, browser: Boolean = false): DisplayActivityInfo {
    return DisplayActivityInfo(
        resolvedInfo = resolveInfo,
        label = resolveInfo.loadLabel(context.packageManager).toString(),
        browser = browser,
        fallback = fallback
    )
}

fun ResolveInfo.toDisplayActivityInfo(context: Context, browser: Boolean = false): DisplayActivityInfo {
    return DisplayActivityInfo(
        resolvedInfo = this,
        label = loadLabel(context.packageManager).toString(),
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
    context: Context,
    sorted: Boolean = true,
    browser: Boolean = false,
): List<DisplayActivityInfo> {
    return map { (_, it) -> it.toDisplayActivityInfo(context, browser) }.labelSorted(sorted)
}

fun Iterable<ResolveInfo>.toPackageKeyedMap() = associateBy { it.activityInfo.packageName }
