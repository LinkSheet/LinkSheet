package fe.linksheet.module.app

import android.content.pm.PackageInfo
import fe.linksheet.image.ImageFactory
import fe.linksheet.module.database.entity.app.AppDomainVerificationState
import fe.linksheet.module.database.entity.app.InstalledApp
import fe.linksheet.module.repository.app.AppDomainVerificationStateRepository
import fe.linksheet.module.repository.app.InstalledAppRepository
import kotlinx.coroutines.flow.*


data class PackageCacheItem(
    val installedApp: InstalledApp,
    val verificationStates: List<AppDomainVerificationState>,
)

class PackageInfoCacheService(
    private val density: () -> Float,
    private val packageInfoService: PackageInfoService,
    private val installedAppRepository: InstalledAppRepository,
    private val appDomainVerificationStateRepository: AppDomainVerificationStateRepository,
) {
    private fun getBestLabel(installedApp: PackageInfo): String {
        val resolveInfo = packageInfoService.getLauncherOrNull(installedApp.packageName)
        if (resolveInfo != null) {
            return packageInfoService.findBestLabel(resolveInfo)
        }

        return packageInfoService.findApplicationLabel(installedApp.applicationInfo)
    }

    private fun createInstalledApp(packageInfo: PackageInfo, sizeDp: Int = 64): InstalledApp {
        val label = getBestLabel(packageInfo)
        val drawable = packageInfoService.getApplicationIcon(packageInfo.applicationInfo)

        val size = Math.round(density() * sizeDp)

        val bitmap = ImageFactory.convertToBitmap(drawable, size, size)
        val iconHash = ImageFactory.hash(bitmap)
        val iconBlob = ImageFactory.compress(bitmap)

        return InstalledApp(packageInfo.packageName, label, packageInfo.applicationInfo.flags, iconHash, iconBlob)
    }

    private fun createDomainVerificationStates(packageInfo: PackageInfo): List<AppDomainVerificationState> {
        val verificationState = packageInfoService.getVerificationState(
            packageInfo.applicationInfo
        ) ?: return emptyList()

        return verificationState.hostToStateMap.map { (domain, state) ->
            AppDomainVerificationState(packageInfo.packageName, domain, state)
        }
    }

    fun getAllInstalled(): Flow<PackageCacheItem> = flow {
        val packages = packageInfoService.getInstalledPackages()
        for (packageInfo in packages) {
            val installedApp = createInstalledApp(packageInfo)
            val states = createDomainVerificationStates(packageInfo)

            installedAppRepository.insert(installedApp)
            appDomainVerificationStateRepository.insert(states)

            val item = PackageCacheItem(installedApp, states)
            emit(item)
        }
    }
}
