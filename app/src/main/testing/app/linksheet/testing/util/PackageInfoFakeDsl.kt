package app.linksheet.testing.util

import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.ResolveInfo
import androidx.compose.ui.graphics.ImageBitmap
import app.linksheet.testing.fake.ImageFakes
import fe.linksheet.extension.android.activityDescriptor
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

    return PackageInfoFake(scope.packageInfo, scope.resolveInfos)
}

@DslMarker
annotation class PackageInfoFakeDsl

@PackageInfoFakeDsl
class PackageInfoFakeScope(val packageInfo: PackageInfo) {
    val resolveInfos = mutableListOf<ResolveInfo>()

    fun activity(name: String, exported: Boolean = true, block: (ActivityScope.() -> Unit)? = null) {
        val activityInfo = ActivityInfo().apply {
            this.name = name
            applicationInfo = packageInfo.applicationInfo
            packageName = packageInfo.packageName
        }

        val scope = ActivityScope(resolveInfos, activityInfo)
        if (block != null) {
            block.invoke(scope)
        } else {
            val resolveInfo = ResolveInfo().apply {
                this.activityInfo = activityInfo
                resolvePackageName = activityInfo.packageName
            }

            resolveInfos.add(resolveInfo)
        }

        activityInfo.targetActivity = scope.targetActivity
        activityInfo.exported = exported
    }
}

@PackageInfoFakeDsl
class ActivityScope(private val resolveInfos: MutableList<ResolveInfo>, val activityInfo: ActivityInfo) {
    var targetActivity: String? = null

    fun addFilter(filter: IntentFilter): ResolveInfo {
        val resolveInfo = ResolveInfo().apply {
            this.activityInfo = this@ActivityScope.activityInfo
            resolvePackageName = activityInfo.packageName
            this.filter = filter
        }

        resolveInfos.add(resolveInfo)
        return resolveInfo
    }

    fun addFilter(block: IntentFilter.() -> Unit = {}): ResolveInfo {
        return addFilter(IntentFilter().apply(block))
    }
}

data class PackageInfoFake(
    val packageInfo: PackageInfo,
    val resolveInfos: List<ResolveInfo>
)

fun packageSetOf(vararg packageInfos: PackageInfoFake): Set<String> {
    return packageInfos.mapTo(LinkedHashSet()) { it.packageName }
}

fun List<PackageInfoFake>.flatResolveInfos(): List<ResolveInfo> {
    return flatMap { it.resolveInfos }
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

fun Iterable<ResolveInfo>.asDescriptors(): List<String> {
    return map { it.activityInfo.activityDescriptor }.distinct()
}
