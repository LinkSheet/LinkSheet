package fe.linksheet.extension

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import fe.linksheet.BuildConfig
import timber.log.Timber

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
        queryFirstIntentActivityByPackageNameOrNull(packageInfo.packageName).also {
            Timber.tag("InstalledPackage").d("${packageInfo.packageName}: $it")
        }
    }.filter { filterRemoveSelfApp(removeSelfApp, it) }
}


fun PackageManager.getInstalledPackagesCompat(flags: Int) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getInstalledPackages(PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else @Suppress("DEPRECATION") getInstalledPackages(flags)


fun PackageManager.queryIntentActivitiesCompat(intent: Intent, flags: Int) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(flags.toLong()))
    } else @Suppress("DEPRECATION") queryIntentActivities(intent, flags)

fun PackageManager.resolveActivityCompat(intent: Intent, flags: Int) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        resolveActivity(intent, PackageManager.ResolveInfoFlags.of(flags.toLong()))
    } else @Suppress("DEPRECATION") resolveActivity(intent, flags)