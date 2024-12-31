package fe.linksheet.extension.android

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import fe.linksheet.module.app.PackageInfoService
import fe.linksheet.module.resolver.DisplayActivityInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Deprecated(message = "Use PackageDisplayInfoHelper")
fun ResolveInfo.toDisplayActivityInfo(context: Context, browser: Boolean = false): DisplayActivityInfo {
    return ResolveInfoCompat.toDisplayActivityInfo(context.packageManager, this, browser)
}

object ResolveInfoCompat : KoinComponent {
    private val packageInfoService by inject<PackageInfoService>()

    @Deprecated(message = "Use PackageDisplayInfoHelper")
    fun toDisplayActivityInfo(
        packageManager: PackageManager,
        resolveInfo: ResolveInfo,
        browser: Boolean = false,
    ): DisplayActivityInfo {
        return packageInfoService.createDisplayActivityInfo(resolveInfo, browser)
    }
}


@Deprecated(message = "Use PackageDisplayInfoHelper")
fun Iterable<ResolveInfo>.toDisplayActivityInfos(
    context: Context,
    sorted: Boolean = true,
    browser: Boolean = false,
): List<DisplayActivityInfo> {
    return map { it.toDisplayActivityInfo(context, browser) }.labelSorted(sorted)
}

fun Map<String, ResolveInfo>.toDisplayActivityInfos(
    packageManager: PackageManager,
    sorted: Boolean = true,
    browser: Boolean = false,
): List<DisplayActivityInfo> {
    return map { (_, it) -> ResolveInfoCompat.toDisplayActivityInfo(packageManager, it, browser) }.labelSorted(sorted)
}

fun Iterable<ResolveInfo>.toPackageKeyedMap() = associateBy { it.activityInfo.packageName }
