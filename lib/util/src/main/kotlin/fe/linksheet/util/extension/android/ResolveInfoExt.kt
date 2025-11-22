package fe.linksheet.util.extension.android

import android.content.pm.ComponentInfo
import android.content.pm.ResolveInfo
import fe.composekit.extension.componentName

val ResolveInfo.info: ComponentInfo
    get() = activityInfo ?: providerInfo ?: serviceInfo

@Deprecated("Should probably be replaced with toComponentNameKeyedMap", replaceWith = ReplaceWith("this.toComponentNameKeyedMap()"))
fun Iterable<ResolveInfo>.toPackageKeyedMap() = associateBy { it.activityInfo.packageName }

fun Iterable<ResolveInfo>.toComponentNameKeyedMap(): Map<String, ResolveInfo> {
    return associateBy { it.activityInfo.componentName.flattenToString() }
}
