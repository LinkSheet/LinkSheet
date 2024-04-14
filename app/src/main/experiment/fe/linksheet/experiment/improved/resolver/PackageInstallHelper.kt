package fe.linksheet.experiment.improved.resolver

import android.content.Context
import android.content.pm.ResolveInfo
import android.content.pm.getAppsWithLauncher
import android.content.pm.hasLauncher
import fe.linksheet.extension.android.toPackageKeyedMap

object PackageInstallHelper {
    fun getLauncherOrNull(context: Context, packageName: String?): ResolveInfo? {
        if (packageName == null) return null
        return context.packageManager.hasLauncher(packageName)
    }

    fun hasLauncher(context: Context, packages: Set<String>): Pair<Set<String>, List<String>> {
        // TODO: Will this cause problems with apps which don't have a launcher?
        val apps = context.packageManager.getAppsWithLauncher().toPackageKeyedMap()

        val noLauncher = packages.filter { it !in apps }
        return apps.keys to noLauncher
    }
}
