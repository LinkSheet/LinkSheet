package fe.linksheet.module.resolver.util

import android.content.Intent
import android.content.pm.ResolveInfo
import fe.linksheet.extension.android.toPackageKeyedMap
import fe.linksheet.util.ResolveInfoFlags

class PackageLauncherHelper(
    private val queryIntentActivities: (Intent, ResolveInfoFlags) -> List<ResolveInfo>
) {
    fun getLauncherOrNull(packageName: String?): ResolveInfo? {
        if (packageName == null) return null

        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(packageName)
        return queryIntentActivities(intent, ResolveInfoFlags.EMPTY).singleOrNull()
    }

    fun hasLauncher(packages: Set<String>): Pair<Set<String>, List<String>> {
        // TODO: Will this cause problems with apps which don't have a launcher?
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = queryIntentActivities(intent, ResolveInfoFlags.EMPTY).toPackageKeyedMap()

        val noLauncher = packages.filter { it !in apps }
        return apps.keys to noLauncher
    }
}
