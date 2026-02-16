package fe.linksheet.util.extension.android

import android.content.pm.ResolveInfo
import fe.composekit.extension.componentName

fun Iterable<ResolveInfo>.toComponentNameKeyedMap(): Map<String, ResolveInfo> {
    return associateBy { it.activityInfo.componentName.flattenToString() }
}
