package fe.linksheet.module.app.`package`

import android.app.ActivityManager
import android.content.pm.ApplicationInfo
import android.content.pm.ComponentInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import fe.std.result.getOrNull
import fe.std.result.tryCatch

@Suppress("FunctionName")
internal fun AndroidPackageIconLoaderModule(
    packageManager: PackageManager,
    activityManager: ActivityManager,
): PackageIconLoader {
    val defaultIcon = packageManager.defaultActivityIcon
    val launcherLargeIconDensity = activityManager.launcherLargeIconDensity

    return DefaultPackageIconLoader(
        defaultIcon = defaultIcon,
        getDrawableForDensity = { packageName, resId ->
            packageManager
                .getResourcesForApplication(packageName)
                .getDrawableForDensity(resId, launcherLargeIconDensity, null)
        },
        loadActivityIcon = { it.loadIcon(packageManager) },
    )
}

interface PackageIconLoader {
    fun loadIcon(componentInfo: ComponentInfo): Drawable
    fun loadApplicationIcon(applicationInfo: ApplicationInfo): Drawable
}

internal class DefaultPackageIconLoader(
    val defaultIcon: Drawable,
    val getDrawableForDensity: (String, Int) -> Drawable?,
    private val loadActivityIcon: (ComponentInfo) -> Drawable,
) : PackageIconLoader {

    override fun loadIcon(componentInfo: ComponentInfo): Drawable {
        val appIcon = loadApplicationIcon(componentInfo.packageName, componentInfo.iconResource)
        if (appIcon != null) return appIcon

        return loadActivityIcon(componentInfo)
    }

    override fun loadApplicationIcon(applicationInfo: ApplicationInfo): Drawable {
        return loadApplicationIcon(applicationInfo.packageName, applicationInfo.icon) ?: defaultIcon
    }

    private fun loadApplicationIcon(packageName: String, resId: Int): Drawable? {
        if (resId == 0) return null

        return tryCatch { getDrawableForDensity(packageName, resId) }.getOrNull()
    }
}
