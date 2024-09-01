package fe.linksheet.experiment.improved.resolver

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.queryIntentActivitiesCompat
import fe.linksheet.extension.android.toPackageKeyedMap

class PackageLauncherHelper(private val packageManager: PackageManager) {
    fun getLauncherOrNull(packageName: String?): ResolveInfo? {
        if (packageName == null) return null

        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(packageName)
        return packageManager.queryIntentActivitiesCompat(intent).singleOrNull()
    }

    fun hasLauncher(packages: Set<String>): Pair<Set<String>, List<String>> {
        // TODO: Will this cause problems with apps which don't have a launcher?
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = packageManager.queryIntentActivitiesCompat(intent).toPackageKeyedMap()

        val noLauncher = packages.filter { it !in apps }
        return apps.keys to noLauncher
    }
}
