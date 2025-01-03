package fe.linksheet.extension.android

import android.content.Context
import android.content.pm.ComponentInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import fe.linksheet.module.app.ActivityAppInfo
import fe.linksheet.module.app.PackageInfoService
import fe.linksheet.module.app.labelSorted
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

val ResolveInfo.info: ComponentInfo
    get() = activityInfo ?: providerInfo ?: serviceInfo

@Deprecated(message = "Use PackageDisplayInfoHelper")
fun ResolveInfo.toAppInfo(context: Context, browser: Boolean = false): ActivityAppInfo {
    return ResolveInfoCompat.toAppInfo(context.packageManager, this, browser)
}

object ResolveInfoCompat : KoinComponent {
    private val packageInfoService by inject<PackageInfoService>()

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

fun Iterable<ResolveInfo>.toPackageKeyedMap() = associateBy { it.activityInfo.packageName }
