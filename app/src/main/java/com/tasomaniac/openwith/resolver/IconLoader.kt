package com.tasomaniac.openwith.resolver

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object IconLoader {
    fun loadFor(context: Context, activity: ActivityInfo): Drawable? {
        try {
            val packageName = activity.packageName
            if (activity.icon != 0) {
                return getIcon(context, packageName, activity.icon)
            }
            val iconRes = activity.iconResource
            if (iconRes != 0) {
                return getIcon(context, packageName, iconRes)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
        return activity.loadIcon(context.packageManager)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun getIcon(context: Context, packageName: String, resId: Int): Drawable? {
        val res = context.packageManager.getResourcesForApplication(packageName)
        return res.getDrawableForDensity(
            resId,
            context.getSystemService(ActivityManager::class.java).launcherLargeIconDensity,
            null
        )
    }
}
