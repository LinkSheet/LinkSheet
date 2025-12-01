@file:Suppress("KotlinRedundantDiagnosticSuppress")

package fe.linksheet.extension.android

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import fe.composekit.core.AndroidVersion

fun PackageManager.getApplicationInfoCompat(packageName: String, flags: Int): ApplicationInfo? {
    return runCatching {
        if (AndroidVersion.isAtLeastApi33T()) {
            getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(flags.toLong()))
        } else getApplicationInfo(packageName, flags)
    }.getOrNull()
}
