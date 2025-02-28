package android.content.pm

import android.content.Intent
import fe.android.compose.version.AndroidVersion
import fe.linksheet.util.ResolveInfoFlags


fun PackageManager.queryIntentActivitiesCompat(intent: Intent, flags: ResolveInfoFlags = ResolveInfoFlags.EMPTY): MutableList<ResolveInfo> {
    return queryIntentActivitiesCompat(intent, flags.value)
}

fun PackageManager.queryIntentActivitiesCompat(intent: Intent, flags: Int = 0): MutableList<ResolveInfo> {
    return if (AndroidVersion.AT_LEAST_API_33_T) queryIntentActivities(
        intent, PackageManager.ResolveInfoFlags.of(flags.toLong())
    )
    else queryIntentActivities(intent, flags)
}

fun PackageManager.getInstalledPackagesCompat(flags: Int = 0): MutableList<PackageInfo> {
    return if (AndroidVersion.AT_LEAST_API_33_T) getInstalledPackages(PackageManager.PackageInfoFlags.of(flags.toLong()))
    else getInstalledPackages(flags)
}


