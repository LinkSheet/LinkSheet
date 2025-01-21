package app.linksheet.testing

import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.ResolveInfo
import androidx.compose.ui.graphics.ImageBitmap
import fe.linksheet.module.app.ActivityAppInfo

fun buildPackageInfoTestFake(packageName: String, name: String, block: PackageInfoFakeScope.() -> Unit): PackageInfoFake {
    val packageInfo = PackageInfo()
    packageInfo.packageName = packageName

    val applicationInfo = ApplicationInfo()
    applicationInfo.name = name
    applicationInfo.packageName = packageInfo.packageName

    packageInfo.applicationInfo = applicationInfo

    val scope = PackageInfoFakeScope(packageInfo)
    block(scope)

    return PackageInfoFake(scope.packageInfo, scope.activities)
}

@DslMarker
annotation class PackageInfoFakeDsl

@PackageInfoFakeDsl
class PackageInfoFakeScope(val packageInfo: PackageInfo) {
    val activities = mutableListOf<ResolveInfo>()

    fun activity(name: String, block: (ActivityInfo.() -> Unit)? = null) {
        val activityInfo = ActivityInfo().apply {
            this.name = name
            applicationInfo = packageInfo.applicationInfo
            packageName = packageInfo.packageName
        }

        block?.invoke(activityInfo)

        val resolveInfo = ResolveInfo().apply {
            this.activityInfo = activityInfo
            resolvePackageName = activityInfo.packageName
        }

        activities.add(resolveInfo)
    }
}

data class PackageInfoFake(
    val packageInfo: PackageInfo,
    val resolveInfos: List<ResolveInfo>
)

fun packageSetOf(vararg packageInfos: PackageInfoFake): Set<String> {
    return packageInfos.mapTo(LinkedHashSet()) { it.packageName }
}

fun listOfFirstActivityResolveInfo(vararg packageInfo: PackageInfoFake): List<ResolveInfo> {
    return packageInfo.mapNotNull { it.firstActivityResolveInfo }
}

fun listOfFirstActivityResolveInfo(packageInfos: List<PackageInfoFake>): List<ResolveInfo> {
    return packageInfos.mapNotNull { it.firstActivityResolveInfo }
}

val PackageInfoFake.packageName: String
    get() = packageInfo.packageName

val PackageInfoFake.firstActivityResolveInfo: ResolveInfo?
    get() = resolveInfos.firstOrNull()

fun PackageInfoFake.toActivityAppInfo(label: String, icon: Lazy<ImageBitmap> = ImageFakes.ImageBitmap): ActivityAppInfo {
    return ActivityAppInfo(firstActivityResolveInfo?.activityInfo!!, label, icon)
}

fun PackageInfoFake.toActivityAppInfo(icon: Lazy<ImageBitmap> = ImageFakes.ImageBitmap): ActivityAppInfo {
    return toActivityAppInfo(packageInfo.applicationInfo!!.name, icon)
}

fun ResolveInfo.toActivityAppInfo(icon: Lazy<ImageBitmap> = ImageFakes.ImageBitmap): ActivityAppInfo {
    return ActivityAppInfo(activityInfo, activityInfo.name, icon)
}

fun Iterable<PackageInfoFake>.toKeyedMap(): Map<String, ResolveInfo> {
    return associate { it.packageName to it.firstActivityResolveInfo!! }
}

fun PackageInfoFake.toKeyedMap(): Map<String, ResolveInfo> {
    return mapOf(this.packageName to firstActivityResolveInfo!!)
}
