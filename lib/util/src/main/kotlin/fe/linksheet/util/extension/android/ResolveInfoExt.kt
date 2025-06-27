package fe.linksheet.util.extension.android

import android.content.pm.ComponentInfo
import android.content.pm.ResolveInfo

val ResolveInfo.info: ComponentInfo
    get() = activityInfo ?: providerInfo ?: serviceInfo

fun Iterable<ResolveInfo>.toPackageKeyedMap() = associateBy { it.activityInfo.packageName }
