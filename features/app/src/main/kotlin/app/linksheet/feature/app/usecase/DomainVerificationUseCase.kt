package app.linksheet.feature.app.usecase

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.verify.domain.DomainVerificationUserState
import androidx.annotation.VisibleForTesting
import app.linksheet.feature.app.AppInfoCreator
import app.linksheet.feature.app.DomainVerificationAppInfo
import app.linksheet.feature.app.LinkHandling
import app.linksheet.feature.app.pkg.PackageIntentHandler
import app.linksheet.feature.app.pkg.domain.DomainVerificationManagerCompat
import app.linksheet.feature.app.pkg.domain.VerificationBrowserState
import app.linksheet.feature.app.pkg.domain.VerificationState
import app.linksheet.feature.app.pkg.domain.VerificationStateCompat
import fe.linksheet.util.ApplicationInfoFlags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DomainVerificationUseCase(
    private val creator: AppInfoCreator,
    private val domainVerificationManager: DomainVerificationManagerCompat,
    private val packageIntentHandler: PackageIntentHandler,
    private val getApplicationInfoOrNull: (String, ApplicationInfoFlags) -> ApplicationInfo?,
    private val getInstalledPackages: () -> List<PackageInfo>,
) {
    fun getVerificationState(applicationInfo: ApplicationInfo): VerificationStateCompat? {
        // TODO: There should be some sort of hybrid state which allows checking the domain verification status of browsers
        // as well, as they may also have domains they can claim ownership over (Brave does this, for example)
        val browsable = packageIntentHandler.findHttpBrowsable(applicationInfo.packageName)
        if (browsable.isNotEmpty()) return VerificationBrowserState

        return domainVerificationManager.getDomainVerificationUserState(applicationInfo.packageName)
    }

    fun getDomainVerificationAppInfos(): List<DomainVerificationAppInfo> {
        val list = mutableListOf<DomainVerificationAppInfo>()
        val packages = getInstalledPackages()
        for (packageInfo in packages) {
            val status = createDomainVerificationAppInfo(packageInfo) ?: continue
            list.add(status)
        }

        return list
    }

    fun getDomainVerificationAppInfoFlow(): Flow<DomainVerificationAppInfo> = flow {
        val packages = getInstalledPackages()
        for (packageInfo in packages) {
            val status = createDomainVerificationAppInfo(packageInfo) ?: continue
            emit(status)
        }
    }

    fun getDomainVerificationAppInfoListFlow(): Flow<List<DomainVerificationAppInfo>> = flow {
        val packages = getInstalledPackages().mapNotNull { packageInfo ->
            createDomainVerificationAppInfo(packageInfo)
        }

        emit(packages)
    }

    fun createDomainVerificationAppInfo(packageName: String): DomainVerificationAppInfo? {
        return getApplicationInfoOrNull(packageName, ApplicationInfoFlags.EMPTY)
            ?.let { createDomainVerificationAppInfo(it, null) }
    }

    fun createDomainVerificationAppInfo(packageInfo: PackageInfo): DomainVerificationAppInfo? {
        val applicationInfo = packageInfo.applicationInfo ?: return null
        val installTime = packageInfo.firstInstallTime
        return createDomainVerificationAppInfo(applicationInfo, installTime)
    }

    @VisibleForTesting
    fun createDomainVerificationAppInfo(applicationInfo: ApplicationInfo, installTime: Long?): DomainVerificationAppInfo? {
        val verificationState = getVerificationState(applicationInfo) ?: return null

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

        val appInfo = creator.toAppInfo(applicationInfo, installTime)
        return DomainVerificationAppInfo(
            appInfo = appInfo,
            linkHandling = linkHandling,
            stateNone = stateNone,
            stateSelected = stateSelected,
            stateVerified = stateVerified
        )
    }
}
