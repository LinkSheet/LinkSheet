package app.linksheet.feature.app.usecase

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import app.linksheet.feature.app.AppInfo
import app.linksheet.feature.app.AppInfoCreator
import app.linksheet.feature.app.pkg.ManifestParser
import app.linksheet.feature.app.pkg.domain.DomainVerificationManagerCompat
import app.linksheet.feature.app.pkg.domain.VerificationState
import app.linksheet.feature.app.pkg.domain.VerificationUnsupportedState
import fe.linksheet.util.ApplicationInfoFlags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class AllAppsUseCase(
    private val creator: AppInfoCreator,
    private val manifestParser: ManifestParser,
    private val domainVerificationManager: DomainVerificationManagerCompat,
    private val getApplicationInfoOrNull: (String, ApplicationInfoFlags) -> ApplicationInfo?,
    private val getInstalledPackages: () -> List<PackageInfo>,
) {
    fun queryAllAppsFlow(): Flow<List<AppInfo>> = flow {
        val apps = queryAllApps()
        emit(apps)
    }

    fun queryAllApps(): List<AppInfo> {
        return getInstalledPackages().mapNotNull { createAppInfo(it) }
    }

    private fun createAppInfo(packageInfo: PackageInfo): AppInfo? {
        val applicationInfo = packageInfo.applicationInfo ?: return null
        val installTime = packageInfo.firstInstallTime
        return creator.toAppInfo(applicationInfo, installTime)
    }

    fun queryApp(packageName: String): AppInfo? {
        return getApplicationInfoOrNull(packageName, ApplicationInfoFlags.EMPTY)
            ?.let { creator.toAppInfo(it, null) }
    }

    fun getAppInfoWithHosts(packageName: String): AppInfoWithHosts? {
        val applicationInfo = getApplicationInfoOrNull(packageName, ApplicationInfoFlags.EMPTY) ?: return null

        val appInfo = creator.toAppInfo(applicationInfo, null)
        val hosts = when (val state = domainVerificationManager.getDomainVerificationUserState(packageName)) {
            null, is VerificationUnsupportedState -> manifestParser.parse(applicationInfo.sourceDir).toSet()
            is VerificationState -> state.hostToStateMap.keys
            else -> return null
        }

        return AppInfoWithHosts(appInfo, hosts)
    }
}

data class AppInfoWithHosts(
    val appInfo: AppInfo,
    val hosts: Set<String>
)
