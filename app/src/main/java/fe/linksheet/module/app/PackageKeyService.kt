package fe.linksheet.module.app

import android.content.pm.ActivityInfo
import fe.linksheet.extension.android.componentName

class PackageKeyService(
    private val checkDisableDeduplicationExperiment: () -> Boolean = { false },
) {
    fun getShortClassName(appPackage: String, clazz: String): String {
        if (!clazz.startsWith(appPackage)) return clazz

        val pn = appPackage.length
        val cn = clazz.length
        if (cn <= pn || clazz[pn] != '.') return clazz

        return clazz.substring(pn, cn)
    }

    fun getDuplicationKey(activityInfo: ActivityInfo): String {
        if(!checkDisableDeduplicationExperiment()) {
            return activityInfo.applicationInfo.packageName
        }

        val componentName = activityInfo.componentName
        val appPackage = activityInfo.applicationInfo.packageName
        val targetActivity = activityInfo.targetActivity ?: ""
        val targetShortClass = getShortClassName(appPackage, targetActivity)

        return componentName.flattenToShortString() + ":" + targetShortClass
    }
}
