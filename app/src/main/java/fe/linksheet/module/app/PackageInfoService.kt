package fe.linksheet.module.app

import android.content.Context
import android.content.Intent
import android.content.pm.*
import android.content.pm.verify.domain.DomainVerificationManager
import android.graphics.drawable.Drawable
import androidx.core.content.getSystemService
import fe.linksheet.extension.android.toPackageKeyedMap
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.ResolveInfoFlags


fun AndroidPackageInfoService(context: Context): PackageInfoService {
    val packageManager = context.packageManager
    val domainVerificationManager = AndroidVersion.atLeastApi31S {
        context.getSystemService<DomainVerificationManager>()
    }

    return PackageInfoService(
        loadLabel = { it.loadLabel(packageManager) },
        getApplicationLabel = packageManager::getApplicationLabel,
        getApplicationIcon = packageManager::getApplicationIcon,
        queryIntentActivities = packageManager::queryIntentActivitiesCompat,
        getInstalledPackages = packageManager::getInstalledPackagesCompat,
        getDomainVerificationUserState = { applicationInfo ->
            AndroidVersion.atLeastApi31S {
                domainVerificationManager!!.getDomainVerificationUserState(applicationInfo.packageName)?.let { state ->
                    VerificationState(state.hostToStateMap, state.isLinkHandlingAllowed)
                }
            }
        },
    )
}

data class VerificationState(
    val hostToStateMap: Map<String, Int>,
    val isLinkHandlingAllowed: Boolean,
)

class PackageInfoService(
    private val loadLabel: (ResolveInfo) -> CharSequence,
    private val getApplicationLabel: (ApplicationInfo) -> CharSequence,
    val getApplicationIcon: (ApplicationInfo) -> Drawable,
    private val queryIntentActivities: (Intent, ResolveInfoFlags) -> List<ResolveInfo>,
    val getInstalledPackages: () -> List<PackageInfo>,
    private val getDomainVerificationUserState: (ApplicationInfo) -> VerificationState?,
) {
    fun findBestLabel(resolveInfo: ResolveInfo): String {
        val label = loadLabel(resolveInfo)
        if (label.isNotEmpty()) return label.toString()

        return findApplicationLabel(resolveInfo.activityInfo.applicationInfo)
    }

    fun findApplicationLabel(applicationInfo: ApplicationInfo): String {
        val appLabel = getApplicationLabel(applicationInfo)
        if (appLabel.isNotEmpty()) return appLabel.toString()

        return applicationInfo.packageName
    }

    fun getVerificationState(applicationInfo: ApplicationInfo): VerificationState? {
        val state = getDomainVerificationUserState(applicationInfo)
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
