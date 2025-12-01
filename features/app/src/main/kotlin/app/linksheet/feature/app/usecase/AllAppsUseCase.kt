package app.linksheet.feature.app.usecase

import android.content.pm.PackageInfo
import app.linksheet.feature.app.AppInfo
import app.linksheet.feature.app.AppInfoCreator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class AllAppsUseCase(
    private val creator: AppInfoCreator,
    private val getInstalledPackages: () -> List<PackageInfo>,
) {
    fun queryAllAppsFlow(): Flow<List<AppInfo>> = flow {
        val apps = queryAllApps()
        emit(apps)
    }

    fun queryAllApps(): List<AppInfo> {
        return getInstalledPackages().mapNotNull { createAppInfo(it) }
    }

    fun createAppInfo(packageInfo: PackageInfo): AppInfo? {
        val applicationInfo = packageInfo.applicationInfo ?: return null
        val installTime = packageInfo.firstInstallTime
        return creator.toAppInfo(applicationInfo, installTime)
    }
}
