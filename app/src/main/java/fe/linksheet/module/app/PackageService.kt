package fe.linksheet.module.app

import android.content.Context
import android.content.pm.*
import android.content.pm.verify.domain.DomainVerificationUserState
import androidx.annotation.VisibleForTesting
import fe.linksheet.extension.android.info
import fe.linksheet.extension.android.toImageBitmap
import fe.linksheet.module.app.`package`.domain.DomainVerificationManagerCompat
import fe.linksheet.module.app.`package`.domain.VerificationBrowserState
import fe.linksheet.module.app.`package`.domain.VerificationState
import fe.linksheet.module.app.`package`.domain.VerificationStateCompat
import fe.linksheet.module.app.`package`.PackageIconLoader
import fe.linksheet.module.app.`package`.PackageLabelService
import fe.linksheet.module.app.`package`.PackageLauncherService
import fe.linksheet.module.app.`package`.DefaultPackageLabelService
import fe.linksheet.module.app.`package`.DefaultPackageLauncherService
import fe.linksheet.module.app.`package`.PackageIntentHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


internal fun AndroidPackageServiceModule(
    context: Context,
    packageIconLoader: PackageIconLoader,
    packageIntentHandler: PackageIntentHandler
): PackageService {
    val packageManager = context.packageManager

    return PackageService(
        domainVerificationManager = DomainVerificationManagerCompat(context),
        packageLabelService = DefaultPackageLabelService(
            loadComponentInfoLabelInternal = { it.loadLabel(packageManager) },
            getApplicationLabel = packageManager::getApplicationLabel,
        ),
        packageLauncherService = DefaultPackageLauncherService(packageManager::queryIntentActivitiesCompat),
        packageIconLoader = packageIconLoader,
        packageIntentHandler = packageIntentHandler,
        getInstalledPackages = packageManager::getInstalledPackagesCompat,
    )
}

class PackageService(
    private val domainVerificationManager: DomainVerificationManagerCompat,
    private val packageLabelService: PackageLabelService,
    private val packageLauncherService: PackageLauncherService,
    private val packageIconLoader: PackageIconLoader,
    private val packageIntentHandler: PackageIntentHandler,
    val getInstalledPackages: () -> List<PackageInfo>,
) : PackageLabelService by packageLabelService,
    PackageLauncherService by packageLauncherService,
    PackageIconLoader by packageIconLoader,
    PackageIntentHandler by packageIntentHandler {

    fun toAppInfo(resolveInfo: ResolveInfo, isBrowser: Boolean): ActivityAppInfo {
        val info = resolveInfo.info

        return ActivityAppInfo(
            componentInfo = info,
            label = loadComponentInfoLabel(info) ?: findApplicationLabel(info.applicationInfo),
            icon = lazy { loadIcon(info).toImageBitmap() }
        )
    }

    fun getVerificationState(applicationInfo: ApplicationInfo): VerificationStateCompat? {
        return domainVerificationManager.getDomainVerificationUserState(applicationInfo.packageName)
            ?: packageIntentHandler.findHttpBrowsable(applicationInfo.packageName)
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
