package fe.linksheet.module.app.`package`

import android.content.Intent
import android.content.pm.ResolveInfo
import fe.linksheet.extension.android.toPackageKeyedMap
import fe.linksheet.util.ResolveInfoFlags
import kotlin.collections.contains

interface PackageLauncherService {
    fun getLauncherOrNull(packageName: String?): ResolveInfo?
    fun hasLauncher(packages: Set<String>): Pair<Set<String>, List<String>>
}

internal class DefaultPackageLauncherService(
    val queryIntentActivities: (Intent, ResolveInfoFlags) -> List<ResolveInfo>
) : PackageLauncherService {

    companion object {
        private val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    }

    override fun getLauncherOrNull(packageName: String?): ResolveInfo? {
        if (packageName == null) return null

        val intent = Intent(launcherIntent).setPackage(packageName)
        val launchers = queryIntentActivities(intent, ResolveInfoFlags.EMPTY)

        return launchers.firstOrNull()
    }

    override fun hasLauncher(packages: Set<String>): Pair<Set<String>, List<String>> {
        // TODO: Will this cause problems with apps which don't have a launcher?
        val apps = queryIntentActivities(launcherIntent, ResolveInfoFlags.EMPTY).toPackageKeyedMap()

        val noLauncher = packages.filter { it !in apps }
        return apps.keys to noLauncher
    }
}
