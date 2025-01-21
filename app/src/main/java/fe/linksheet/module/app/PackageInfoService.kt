package fe.linksheet.module.app

import android.content.Context
import android.content.Intent
import android.content.pm.*
import android.content.pm.verify.domain.DomainVerificationUserState
import android.graphics.drawable.Drawable
import androidx.annotation.VisibleForTesting
import fe.linksheet.extension.android.info
import fe.linksheet.extension.android.toImageBitmap
import fe.linksheet.module.app.domain.DomainVerificationManagerCompat
import fe.linksheet.module.app.domain.VerificationBrowserState
import fe.linksheet.module.app.domain.VerificationState
import fe.linksheet.module.app.domain.VerificationStateCompat
import fe.linksheet.util.ResolveInfoFlags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


internal fun AndroidPackageInfoModule(context: Context, iconLoader: PackageIconLoader): PackageInfoService {
    val packageManager = context.packageManager
    val domainVerificationManager = DomainVerificationManagerCompat(context)

    val packageInfoLabelService = RealPackageInfoLabelService(
        loadComponentInfoLabelInternal = { it.loadLabel(packageManager) },
        getApplicationLabel = packageManager::getApplicationLabel,
    )

    val packageInfoLauncherService = RealPackageInfoLauncherService(
        queryIntentActivities = packageManager::queryIntentActivitiesCompat,
    )

    val packageInfoBrowserService = RealPackageInfoBrowserService(
        queryIntentActivities = packageManager::queryIntentActivitiesCompat,
    )

    return PackageInfoService(
        domainVerificationManager = domainVerificationManager,
        packageLabelService = packageInfoLabelService,
        packageInfoLauncherService = packageInfoLauncherService,
        packageInfoBrowserService = packageInfoBrowserService,
        queryIntentActivities = packageManager::queryIntentActivitiesCompat,
        loadIcon = iconLoader::loadIcon,
        loadApplicationIcon = iconLoader::loadApplicationIcon,
        getInstalledPackages = packageManager::getInstalledPackagesCompat,
    )
}

class PackageInfoService(
    private val domainVerificationManager: DomainVerificationManagerCompat,
    private val packageLabelService: PackageInfoLabelService,
    private val packageInfoLauncherService: PackageInfoLauncherService,
    private val packageInfoBrowserService: PackageInfoBrowserService,
    val queryIntentActivities: (Intent, ResolveInfoFlags) -> List<ResolveInfo>,
    val loadIcon: (ComponentInfo) -> Drawable,
    val loadApplicationIcon: (ApplicationInfo) -> Drawable,
    val getInstalledPackages: () -> List<PackageInfo>,
) : PackageInfoLabelService by packageLabelService,
    PackageInfoLauncherService by packageInfoLauncherService,
    PackageInfoBrowserService by packageInfoBrowserService {

    fun toAppInfo(resolveInfo: ResolveInfo, isBrowser: Boolean): ActivityAppInfo {
        val info = resolveInfo.info
        val packageName = info.packageName

        return ActivityAppInfo(
            componentInfo = info,
            label = loadComponentInfoLabel(info) ?: packageName,
            icon = lazy { loadIcon(info).toImageBitmap() }
        )
    }

    fun getVerificationState(applicationInfo: ApplicationInfo): VerificationStateCompat? {
        return domainVerificationManager.getDomainVerificationUserState(applicationInfo.packageName)
            ?: getBrowsableResolveInfos(applicationInfo.packageName)
                ?.takeIf { it.isNotEmpty() }
                ?.let { VerificationBrowserState }
    }

    fun getDomainVerificationAppInfos(): Flow<DomainVerificationAppInfo> = flow {
        val packages = getInstalledPackages()
        for (packageInfo in packages) {
            val status = createDomainVerificationAppInfo(packageInfo) ?: continue
            emit(status)
        }
    }


    @VisibleForTesting
    fun createDomainVerificationAppInfo(packageInfo: PackageInfo): DomainVerificationAppInfo? {
        val applicationInfo = packageInfo.applicationInfo ?: return null
        val verificationState = getVerificationState(applicationInfo) ?: return null
        val label = findBestLabel(packageInfo, getLauncherOrNull(packageInfo.packageName))

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
            is VerificationBrowserState -> LinkHandling.Browser
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

        return status
    }
}
