package fe.linksheet.extension

import android.content.Context
import android.content.pm.ResolveInfo
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import com.tasomaniac.openwith.resolver.IconLoader

fun ResolveInfo.toDisplayActivityInfo(context: Context): DisplayActivityInfo {
    return DisplayActivityInfo(
        activityInfo = activityInfo,
        displayLabel = loadLabel(context.packageManager).toString()
    ).apply {
        displayIcon = IconLoader.loadFor(context, activityInfo)
    }
}


fun Iterable<ResolveInfo>.toDisplayActivityInfo(context: Context, sorted: Boolean = true): Iterable<DisplayActivityInfo> {
    val displayActivityInfos = map { it.toDisplayActivityInfo(context) }
    return if (sorted) {
        displayActivityInfos.sortedBy { it.displayLabel.lowercase() }
    } else displayActivityInfos
}

fun Iterable<ResolveInfo>.toPackageKeyedMap() = associateBy { it.activityInfo.packageName }