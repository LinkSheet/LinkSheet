package fe.linksheet.module.app

import android.app.ActivityManager
import android.content.pm.ApplicationInfo
import android.content.pm.ComponentInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import fe.std.result.getOrNull
import fe.std.result.tryCatch

internal fun PackageIconLoaderModule(
    packageManager: PackageManager,
    activityManager: ActivityManager,
): PackageIconLoader {
    val defaultIcon  =packageManager.defaultActivityIcon
    val launcherLargeIconDensity = activityManager.launcherLargeIconDensity

    return PackageIconLoader(
        defaultIcon = defaultIcon,
        density = launcherLargeIconDensity,
        getResourcesForApplication = packageManager::getResourcesForApplication,
        loadActivityIcon = { it.loadIcon(packageManager) },
    )
}

class PackageIconLoader(
    val defaultIcon: Drawable,
    val density: Int,
    val getResourcesForApplication: (String) -> Resources,
    private val loadActivityIcon: (ComponentInfo) -> Drawable,
) {
    fun loadIcon(componentInfo: ComponentInfo): Drawable {
        val appIcon = loadApplicationIcon(componentInfo.packageName, componentInfo.iconResource)
        if (appIcon != null) return appIcon

        return loadActivityIcon(componentInfo)
    }

    fun loadApplicationIcon(applicationInfo: ApplicationInfo): Drawable {
        return loadApplicationIcon(applicationInfo.packageName, applicationInfo.icon) ?: defaultIcon
    }

    private fun loadApplicationIcon(packageName: String, resId: Int): Drawable? {
        if (resId == 0) return null

        return tryCatch {
            getResourcesForApplication(packageName).getDrawableForDensity(resId, density, null)
        }.getOrNull()
    }
}
