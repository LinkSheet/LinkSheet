package app.linksheet.feature.shizuku.usecase

import android.content.pm.PackageInfo
import app.linksheet.feature.app.core.AppInfo
import app.linksheet.feature.app.core.AppInfoCreator
import app.linksheet.feature.shizuku.service.declaresManagerProvider
import fe.composekit.flag.PackageInfoFlags

class ShizukuManagerUseCase internal constructor(
    private val creator: AppInfoCreator,
    private val selfPackage: String,
    private val getInstalledPackages: (PackageInfoFlags) -> List<PackageInfo>,
    private val getCertSha256: (String) -> String?,
) {
    fun queryManagers(): List<AppInfo> {
        return getInstalledPackages(PackageInfoFlags.select(PackageInfoFlags.GET_PROVIDERS))
            .filter { it.packageName != selfPackage }
            .filter { info -> declaresManagerProvider(info.providers?.map { it.name }.orEmpty()) }
            .mapNotNull { info ->
                val applicationInfo = info.applicationInfo ?: return@mapNotNull null
                creator.toAppInfo(applicationInfo, info.firstInstallTime)
            }
    }

    fun certSha256(packageName: String): String? = getCertSha256(packageName)
}
