package app.linksheet.testing.fake

import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import app.linksheet.feature.app.*
import app.linksheet.testing.util.PackageInfoFake
import app.linksheet.testing.util.firstActivityResolveInfo
import fe.android.compose.icon.BitmapIconPainter
import fe.android.compose.icon.IconPainter

fun PackageInfoFake.toActivityAppInfo(
    label: String,
    icon: Drawable = ImageFakes.EmptyDrawable,
): ActivityAppInfo {
    val componentInfo = firstActivityResolveInfo?.activityInfo!!
    val appInfo = AppInfo(
        packageName = componentInfo.packageName,
        label = label,
        icon= BitmapIconPainter.drawable(icon),
        0,
    )
    return ActivityAppInfo(appInfo, componentInfo)
}

fun PackageInfoFake.toActivityAppInfo(icon: Drawable = ImageFakes.EmptyDrawable): ActivityAppInfo {
    return toActivityAppInfo(packageInfo.applicationInfo!!.name, icon)
}

fun ResolveInfo.toActivityAppInfo(icon: Drawable = ImageFakes.EmptyDrawable): ActivityAppInfo {
    val appInfo = AppInfo(
        packageName = activityInfo.packageName,
        label = activityInfo.name,
        icon= BitmapIconPainter.drawable(icon),
        0,
    )
    return ActivityAppInfo(appInfo, activityInfo)
}
fun PackageInfoFake.toDomainVerificationAppInfo(
    linkHandling: LinkHandling,
    stateNone: MutableList<String>,
    stateSelected: MutableList<String>,
    stateVerified: MutableList<String>,
    icon: IconPainter
): DomainVerificationAppInfo {
    val appInfo = AppInfo(
        packageName = packageInfo.applicationInfo!!.packageName,
        label = packageInfo.applicationInfo!!.name,
        icon = icon,
        flags = 0,
    )
    return DomainVerificationAppInfo(
        appInfo,
        linkHandling,
        stateNone,
        stateSelected,
        stateVerified
    )
}

fun PackageInfoFake.toDomainVerificationAppInfo(
    linkHandling: LinkHandling,
    stateNone: MutableList<String>,
    stateSelected: MutableList<String>,
    stateVerified: MutableList<String>,
    icon: Drawable = ImageFakes.EmptyDrawable,
): DomainVerificationAppInfo {
    val appInfo = AppInfo(
        packageName = packageInfo.applicationInfo!!.packageName,
        label = packageInfo.applicationInfo!!.name,
        icon = BitmapIconPainter.drawable(icon),
        flags = 0,
    )
    return DomainVerificationAppInfo(
        appInfo,
        linkHandling,
        stateNone,
        stateSelected,
        stateVerified,
    )
}

fun Iterable<ResolveInfo>.asDescriptors(): List<String> {
    return map { it.activityInfo.activityDescriptor }.distinct()
}

fun ActivityAppInfo.asDescriptor(): String {
    return PackageIdHelper.getDescriptor(this)
}
