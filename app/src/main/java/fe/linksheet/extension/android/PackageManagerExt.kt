package fe.linksheet.extension.android

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import fe.linksheet.BuildConfig
import fe.linksheet.util.AndroidVersion

fun PackageManager.queryFirstIntentActivityByPackageNameOrNull(packageName: String): ResolveInfo? {
    val intent = Intent().setPackage(packageName)
    // TODO: doesn't this do the same thing twice? addCategory mutates the intent
    return queryResolveInfosByIntent(intent.addCategory(Intent.CATEGORY_LAUNCHER)).firstOrNull()
        ?: queryResolveInfosByIntent(intent).firstOrNull()
}

val filterRemoveSelfApp: (Boolean, ResolveInfo) -> Boolean = { removeSelfApp, resolveInfo ->
    !removeSelfApp || resolveInfo.activityInfo.packageName != BuildConfig.APPLICATION_ID
}

fun PackageManager.queryResolveInfosByIntent(
    intent: Intent,
    removeSelfApp: Boolean = false
): List<ResolveInfo> {
    return queryIntentActivitiesCompat(intent, PackageManager.MATCH_ALL).filter {
        filterRemoveSelfApp(removeSelfApp, it)
    }
}

fun PackageManager.queryAllResolveInfos(
    removeSelfApp: Boolean = false
): List<ResolveInfo> {
    return getInstalledPackagesCompat(PackageManager.MATCH_ALL).mapNotNull { packageInfo ->
        queryFirstIntentActivityByPackageNameOrNull(packageInfo.packageName)
    }.filter { filterRemoveSelfApp(removeSelfApp, it) }
}


fun PackageManager.getInstalledPackagesCompat(
    flags: Int
): MutableList<PackageInfo> = if (AndroidVersion.AT_LEAST_API_33_T) {
    getInstalledPackages(PackageManager.PackageInfoFlags.of(flags.toLong()))
} else @Suppress("DEPRECATION") getInstalledPackages(flags)


fun PackageManager.queryIntentActivitiesCompat(
    intent: Intent,
    flags: Int
): MutableList<ResolveInfo> = if (AndroidVersion.AT_LEAST_API_33_T) {
    queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(flags.toLong()))
} else @Suppress("DEPRECATION") queryIntentActivities(intent, flags)

fun PackageManager.resolveActivityCompat(
    intent: Intent,
    flags: Int
) = if (AndroidVersion.AT_LEAST_API_33_T) {
    resolveActivity(intent, PackageManager.ResolveInfoFlags.of(flags.toLong()))
} else @Suppress("DEPRECATION") resolveActivity(intent, flags)

fun PackageManager.getApplicationInfoCompat(packageName: String, flags: Int): ApplicationInfo? {
    return runCatching {
        if (AndroidVersion.AT_LEAST_API_33_T) {
            getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(flags.toLong()))
        } else {
            @Suppress("DEPRECATION")
            getApplicationInfo(packageName, flags)
        }
    }.getOrNull()
}