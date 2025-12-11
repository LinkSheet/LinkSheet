package app.linksheet.feature.app

import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import app.linksheet.feature.app.pkg.PackageIconLoader
import app.linksheet.feature.app.pkg.PackageLabelService
import app.linksheet.feature.app.pkg.PackageLauncherService
import fe.android.compose.icon.BitmapIconPainter
import fe.composekit.extension.info

class AppInfoCreator(
    private val packageLabelService: PackageLabelService,
    private val packageLauncherService: PackageLauncherService,
    private val packageIconLoader: PackageIconLoader,
) {
    fun toActivityAppInfo(resolveInfo: ResolveInfo, installTime: Long?): ActivityAppInfo {
        val info = resolveInfo.info

        return ActivityAppInfo(
            appInfo = toAppInfo(info.applicationInfo, installTime),
            componentInfo = info,
        )
    }
    fun toAppInfo(applicationInfo: ApplicationInfo, installTime: Long?): AppInfo {
        val launcher = packageLauncherService.getLauncherOrNull(applicationInfo.packageName)
        val label = packageLabelService.findBestLabel(applicationInfo, launcher)
        val icon = packageIconLoader.loadApplicationIcon(applicationInfo)

        return AppInfo(
            packageName = applicationInfo.packageName,
            label = label,
            icon = BitmapIconPainter.drawable(icon),
            flags = applicationInfo.flags,
            installTime = installTime
        )
    }
}
