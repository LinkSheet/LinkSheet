package fe.linksheet.feature.app.`package`

import android.content.pm.ApplicationInfo
import android.content.pm.ComponentInfo
import android.graphics.drawable.Drawable
import fe.std.result.getOrNull
import fe.std.result.tryCatch

interface PackageIconLoader {
    fun loadIcon(componentInfo: ComponentInfo): Drawable
    fun loadApplicationIcon(applicationInfo: ApplicationInfo): Drawable
}

class DefaultPackageIconLoader(
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
