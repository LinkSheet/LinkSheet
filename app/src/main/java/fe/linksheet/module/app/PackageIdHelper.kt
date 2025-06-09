package fe.linksheet.module.app

import android.content.ComponentName
import android.content.pm.ActivityInfo
import fe.linksheet.extension.android.componentName

object PackageIdHelper {
    private fun getShortClassName(appPackage: String, clazz: String): String {
        if (!clazz.startsWith(appPackage)) return clazz

        val pn = appPackage.length
        val cn = clazz.length
        if (cn <= pn || clazz[pn] != '.') return clazz

        return clazz.substring(pn, cn)
    }

    private fun createDescriptor(componentName: ComponentName, appPackage: String, targetActivity: String): String {
        val targetShortClass = getShortClassName(appPackage, targetActivity)
        return "${componentName.flattenToShortString()}:$targetShortClass"
    }

    fun getDescriptor(activityInfo: ActivityInfo): String {
        val componentName = activityInfo.componentName
        val appPackage = activityInfo.applicationInfo.packageName
        val targetActivity = activityInfo.targetActivity ?: ""
        return createDescriptor(componentName, appPackage, targetActivity)
    }

    fun getDescriptor(activityAppInfo: ActivityAppInfo): String {
        val componentName = activityAppInfo.componentName
        val appPackage = activityAppInfo.packageName
        return createDescriptor(componentName, appPackage, "")
    }
}
