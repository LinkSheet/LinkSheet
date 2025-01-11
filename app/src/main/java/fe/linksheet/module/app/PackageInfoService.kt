package fe.linksheet.module.app

import android.content.Context
import android.content.Intent
import android.content.pm.*
import android.content.pm.verify.domain.DomainVerificationUserState
import android.graphics.drawable.Drawable
import fe.linksheet.extension.android.info
import fe.linksheet.extension.android.toImageBitmap
import fe.linksheet.extension.android.toPackageKeyedMap
import fe.linksheet.module.app.domain.DomainVerificationManagerCompat
import fe.linksheet.module.app.domain.VerificationState
import fe.linksheet.module.app.domain.VerificationStateCompat
import fe.linksheet.util.ResolveInfoFlags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


internal fun AndroidPackageInfoModule(context: Context, iconLoader: PackageIconLoader): PackageInfoService {
    val packageManager = context.packageManager
    val domainVerificationManager = DomainVerificationManagerCompat(context)

    return PackageInfoService(
        domainVerificationManager = domainVerificationManager,
        loadComponentInfoLabelInternal = { it.loadLabel(packageManager) },
        getApplicationLabel = packageManager::getApplicationLabel,
        loadIcon = iconLoader::loadIcon,
        loadApplicationIcon = iconLoader::loadApplicationIcon,
        queryIntentActivities = packageManager::queryIntentActivitiesCompat,
        getInstalledPackages = packageManager::getInstalledPackagesCompat,
    )
}

class PackageInfoService(
    private val domainVerificationManager: DomainVerificationManagerCompat,
    private val loadComponentInfoLabelInternal: (ComponentInfo) -> CharSequence,
    private val getApplicationLabel: (ApplicationInfo) -> CharSequence,
    val loadIcon: (ComponentInfo) -> Drawable,
    val loadApplicationIcon: (ApplicationInfo) -> Drawable,
    val queryIntentActivities: (Intent, ResolveInfoFlags) -> List<ResolveInfo>,
    val getInstalledPackages: () -> List<PackageInfo>,
) {
    fun toAppInfo(resolveInfo: ResolveInfo, isBrowser: Boolean): ActivityAppInfo {
        val info = resolveInfo.info
        val packageName = info.packageName

        return ActivityAppInfo(
            componentInfo = info,
            label = loadComponentInfoLabel(info) ?: packageName,
            icon = lazy { loadIcon(info).toImageBitmap() }
        )
    }

    fun loadComponentInfoLabel(info: ComponentInfo): String? {
        val label = loadComponentInfoLabelInternal(info)
        if (label.isNotEmpty()) return label.toString()

        return null
    }

    fun findBestLabel(packageInfo: PackageInfo): String {
        val resolveInfo = getLauncherOrNull(packageInfo.packageName)
        if (resolveInfo != null) {
            val label = loadComponentInfoLabel(resolveInfo.info)
            if (label != null) return label
        }

        return packageInfo.applicationInfo?.let(::findApplicationLabel) ?: packageInfo.packageName
    }

    fun findApplicationLabel(applicationInfo: ApplicationInfo): String? {
        val appLabel = getApplicationLabel(applicationInfo)
        if (appLabel.isNotEmpty()) return appLabel.toString()

        return applicationInfo.packageName
    }

    fun getVerificationState(applicationInfo: ApplicationInfo): VerificationStateCompat? {
        return domainVerificationManager.getDomainVerificationUserState(applicationInfo.packageName)
    }

    companion object {
        private val launcherIntent = { Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER) }
    }

    fun getLauncherOrNull(packageName: String?): ResolveInfo? {
        if (packageName == null) return null

        val intent = launcherIntent().setPackage(packageName)
        val launchers = queryIntentActivities(intent, ResolveInfoFlags.EMPTY)

        return launchers.firstOrNull()
    }

    fun hasLauncher(packages: Set<String>): Pair<Set<String>, List<String>> {
        // TODO: Will this cause problems with apps which don't have a launcher?
        val intent = launcherIntent()
        val apps = queryIntentActivities(intent, ResolveInfoFlags.EMPTY).toPackageKeyedMap()

        val noLauncher = packages.filter { it !in apps }
        return apps.keys to noLauncher
    }

    fun getDomainVerificationAppInfos(): Flow<DomainVerificationAppInfo> = flow {
        val packages = getInstalledPackages()
        for (packageInfo in packages) {
            val applicationInfo = packageInfo.applicationInfo ?: continue
            val verificationState = getVerificationState(applicationInfo) ?: continue
            val label = findBestLabel(packageInfo)

            val stateNone = mutableListOf<String>()
            val stateSelected = mutableListOf<String>()
            val stateVerified = mutableListOf<String>()

            if (verificationState is VerificationState) {
                for ((domain, state) in verificationState.hostToStateMap) {
                    when (state) {
                        DomainVerificationUserState.DOMAIN_STATE_NONE -> stateNone.add(domain)
                        DomainVerificationUserState.DOMAIN_STATE_SELECTED -> stateSelected.add(domain)
                        DomainVerificationUserState.DOMAIN_STATE_VERIFIED -> stateVerified.add(domain)
                    }
                }
            }

            val linkHandling = when (verificationState) {
                is VerificationState if verificationState.isLinkHandlingAllowed -> LinkHandling.Allowed
                is VerificationState -> LinkHandling.Disallowed
                else -> LinkHandling.Unsupported
            }

            val status = DomainVerificationAppInfo(
                packageInfo.packageName,
                label,
                lazy { loadApplicationIcon(applicationInfo).toImageBitmap() },
                applicationInfo.flags,
                linkHandling,
                stateNone,
                stateSelected,
                stateVerified
            )

            emit(status)
        }
    }
}
