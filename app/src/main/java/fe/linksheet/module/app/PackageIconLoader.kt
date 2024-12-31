package fe.linksheet.module.app

import android.app.ActivityManager
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.graphics.drawable.Drawable
import fe.std.result.getOrNull
import fe.std.result.tryCatch

fun PackageIconLoaderModule(packageManager: PackageManager, activityManager: ActivityManager): PackageIconLoader {
    val launcherLargeIconDensity = activityManager.launcherLargeIconDensity

    return PackageIconLoader(
        density = launcherLargeIconDensity,
        getResourcesForApplication = packageManager::getResourcesForApplication,
        loadActivityIcon = { it.loadIcon(packageManager) },
    )
}

class PackageIconLoader(
    val density: Int,
    val getResourcesForApplication: (String) -> Resources,
    private val loadActivityIcon: (ActivityInfo) -> Drawable,
) {
    fun loadIcon(activityInfo: ActivityInfo): Drawable? {
        val appIcon = loadApplicationIcon(activityInfo.packageName, activityInfo.iconResource)
        if (appIcon != null) return appIcon

        return loadActivityIcon(activityInfo)
    }

    fun loadIcon(resolveInfo: ResolveInfo): Drawable? {
        return loadIcon(resolveInfo.activityInfo)
    }

    fun loadApplicationIcon(packageName: String, resId: Int): Drawable? {
        if (resId == 0) return null

        return tryCatch {
            getResourcesForApplication(packageName).getDrawableForDensity(resId, density, null)
        }.getOrNull()
    }
}
