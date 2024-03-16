package fe.linksheet.util

import android.content.pm.ResolveInfo

object LinkSheetCompat {
    private val packages = setOf("fe.linksheet.compat", "fe.linksheet.compat.debug")

    fun isCompat(resolveInfo: ResolveInfo): Boolean {
        return isCompat(resolveInfo.activityInfo.packageName)
    }

    fun isCompat(pkg: String): Boolean {
        return pkg in packages
    }
}
