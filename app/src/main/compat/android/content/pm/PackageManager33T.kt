package android.content.pm

import android.content.Intent
import fe.composekit.core.AndroidVersion
import fe.linksheet.util.ApplicationInfoFlags
import fe.linksheet.util.ResolveInfoFlags

fun PackageManager.resolveActivityCompat(
    intent: Intent,
    flags: ResolveInfoFlags = ResolveInfoFlags.EMPTY,
): ResolveInfo? {
    return when {
        AndroidVersion.isAtLeastApi33T() -> resolveActivity(
            intent,
            PackageManager.ResolveInfoFlags.of(flags.value.toLong())
        )

        else -> resolveActivity(intent, flags.value)
    }
}

fun PackageManager.queryIntentActivitiesCompat(
    intent: Intent,
    flags: ResolveInfoFlags = ResolveInfoFlags.EMPTY,
): MutableList<ResolveInfo> {
    return queryIntentActivitiesCompat(intent, flags.value)
}

fun PackageManager.queryIntentActivitiesCompat(intent: Intent, flags: Int = 0): MutableList<ResolveInfo> {
    return when {
        AndroidVersion.isAtLeastApi33T() -> queryIntentActivities(
            intent, PackageManager.ResolveInfoFlags.of(flags.toLong())
        )

        else -> queryIntentActivities(intent, flags)
    }
}

fun PackageManager.getInstalledPackagesCompat(flags: Int = 0): MutableList<PackageInfo> {
    return if (AndroidVersion.isAtLeastApi33T()) getInstalledPackages(PackageManager.PackageInfoFlags.of(flags.toLong()))
    else getInstalledPackages(flags)
}

fun PackageManager.getApplicationInfoCompat(
    packageName: String,
    flags: ApplicationInfoFlags = ApplicationInfoFlags.EMPTY
): ApplicationInfo {
    return if (AndroidVersion.isAtLeastApi33T()) getApplicationInfo(
        packageName,
        PackageManager.ApplicationInfoFlags.of(flags.value.toLong())
    )
    else getApplicationInfo(packageName, flags.value)
}
