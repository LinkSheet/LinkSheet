package fe.linksheet.module.app

import android.content.Context
import android.content.Intent
import android.content.pm.*
import android.graphics.drawable.Drawable
import fe.linksheet.extension.android.toImageBitmap
import fe.linksheet.extension.android.toPackageKeyedMap
import fe.linksheet.module.resolver.DisplayActivityInfo
import fe.linksheet.util.ResolveInfoFlags


internal fun AndroidPackageInfoModule(context: Context, iconLoader: PackageIconLoader): PackageInfoService {
    val packageManager = context.packageManager
    val domainVerificationManager = DomainVerificationManagerCompat(context)

    return PackageInfoService(
        domainVerificationManager = domainVerificationManager,
        loadLabel = { it.loadLabel(packageManager) },
        getApplicationLabel = packageManager::getApplicationLabel,
        loadIcon = iconLoader::loadIcon,
        queryIntentActivities = packageManager::queryIntentActivitiesCompat,
        getInstalledPackages = packageManager::getInstalledPackagesCompat,
    )
}

class PackageInfoService(
    private val domainVerificationManager: DomainVerificationManagerCompat,
    private val loadLabel: (ResolveInfo) -> CharSequence,
    private val getApplicationLabel: (ApplicationInfo) -> CharSequence,
    val loadIcon: (ResolveInfo) -> Drawable?,
    val queryIntentActivities: (Intent, ResolveInfoFlags) -> List<ResolveInfo>,
    val getInstalledPackages: () -> List<PackageInfo>,
) {
    fun toAppInfo(resolveInfo: ResolveInfo, isBrowser: Boolean): ActivityAppInfo {
        val packageName = resolveInfo.activityInfo.packageName

        return ActivityAppInfo(
            activityInfo = resolveInfo.activityInfo,
            label = findBestLabel(resolveInfo) ?: packageName,
            icon = lazy { loadIcon(resolveInfo)?.toImageBitmap()!! }
        )
    }

    fun createDisplayActivityInfo(resolveInfo: ResolveInfo, isBrowser: Boolean): DisplayActivityInfo {
        return DisplayActivityInfo(
            resolvedInfo = resolveInfo,
            // TODO: Find a better way to do this
            label = findBestLabel(resolveInfo) ?: resolveInfo.resolvePackageName,
            browser = isBrowser,
            icon = lazy { loadIcon(resolveInfo)?.toImageBitmap()!! }
        )
    }

    fun findBestLabel(resolveInfo: ResolveInfo): String? {
        val label = loadLabel(resolveInfo)
        if (label.isNotEmpty()) return label.toString()

        return null
    }

    fun findBestLabel(packageInfo: PackageInfo): String {
        val resolveInfo = getLauncherOrNull(packageInfo.packageName)
        if (resolveInfo != null) {
            val label = findBestLabel(resolveInfo)
            if (label != null) return label
        }

        return packageInfo.applicationInfo?.let(::findApplicationLabel) ?: packageInfo.packageName
    }

    fun findApplicationLabel(applicationInfo: ApplicationInfo): String? {
        val appLabel = getApplicationLabel(applicationInfo)
        if (appLabel.isNotEmpty()) return appLabel.toString()

        return applicationInfo.packageName
    }

    fun getVerificationState(applicationInfo: ApplicationInfo): VerificationState? {
        val state = domainVerificationManager.getDomainVerificationUserState(applicationInfo.packageName)
        if (state?.hostToStateMap?.isEmpty() == true) return null

        return state
    }

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
