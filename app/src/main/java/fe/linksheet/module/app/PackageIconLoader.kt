package fe.linksheet.module.app

import android.app.ActivityManager
import android.content.pm.ComponentInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.graphics.drawable.Drawable
import fe.linksheet.extension.android.info
import fe.std.result.getOrNull
import fe.std.result.tryCatch

internal fun PackageIconLoaderModule(packageManager: PackageManager, activityManager: ActivityManager): PackageIconLoader {
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
    private val loadActivityIcon: (ComponentInfo) -> Drawable,
) {
    fun loadIcon(componentInfo: ComponentInfo): Drawable? {
        val appIcon = loadApplicationIcon(componentInfo.packageName, componentInfo.iconResource)
        if (appIcon != null) return appIcon

        return loadActivityIcon(componentInfo)
    }

    fun loadIcon(resolveInfo: ResolveInfo): Drawable? {
        return loadIcon(resolveInfo.info)
    }

    private fun loadApplicationIcon(packageName: String, resId: Int): Drawable? {
        if (resId == 0) return null

        return tryCatch {
            getResourcesForApplication(packageName).getDrawableForDensity(resId, density, null)
        }.getOrNull()
    }
}
