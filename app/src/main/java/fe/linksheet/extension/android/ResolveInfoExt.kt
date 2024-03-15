package fe.linksheet.extension.android

import android.content.Context
import android.content.pm.ResolveInfo
import fe.linksheet.resolver.DisplayActivityInfo

fun ResolveInfo.toDisplayActivityInfo(context: Context) = DisplayActivityInfo(
    resolvedInfo = this,
    label = loadLabel(context.packageManager).toString(),
)

fun Iterable<ResolveInfo>.toDisplayActivityInfos(
    context: Context,
    sorted: Boolean = true
): List<DisplayActivityInfo> {
    return map { it.toDisplayActivityInfo(context) }.labelSorted(sorted)
}

fun Map<String, ResolveInfo>.toDisplayActivityInfos(
    context: Context,
    sorted: Boolean = true
): List<DisplayActivityInfo> {
    return map { (_, it) -> it.toDisplayActivityInfo(context) }.labelSorted(sorted)
}

fun Iterable<ResolveInfo>.toPackageKeyedMap() = associateBy { it.activityInfo.packageName }
