package fe.linksheet.extension.android

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import app.linksheet.feature.app.ActivityAppInfo
import app.linksheet.feature.app.PackageService
import app.linksheet.feature.app.labelSorted
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Deprecated(message = "Use PackageDisplayInfoHelper")
fun ResolveInfo.toAppInfo(context: Context, browser: Boolean = false): ActivityAppInfo {
    return ResolveInfoCompat.toAppInfo(context.packageManager, this, browser)
}

object ResolveInfoCompat : KoinComponent {
    private val packageInfoService by inject<PackageService>()

    @Deprecated(message = "Use PackageDisplayInfoHelper")
    fun toAppInfo(
        packageManager: PackageManager,
        resolveInfo: ResolveInfo,
        browser: Boolean = false,
    ): ActivityAppInfo {
        return packageInfoService.toAppInfo(resolveInfo, browser)
    }
}


@Deprecated(message = "Use PackageDisplayInfoHelper")
fun Iterable<ResolveInfo>.toDisplayActivityInfos(
    context: Context,
    sorted: Boolean = true,
    browser: Boolean = false,
): List<ActivityAppInfo> {
    return map { it.toAppInfo(context, browser) }.labelSorted(sorted)
}

fun Map<String, ResolveInfo>.toDisplayActivityInfos(
    packageManager: PackageManager,
    sorted: Boolean = true,
    browser: Boolean = false,
): List<ActivityAppInfo> {
    return map { (_, it) -> ResolveInfoCompat.toAppInfo(packageManager, it, browser) }.labelSorted(sorted)
}
