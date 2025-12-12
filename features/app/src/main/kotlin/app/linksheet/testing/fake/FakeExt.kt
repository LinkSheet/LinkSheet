package app.linksheet.testing.fake

import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import app.linksheet.feature.app.core.*
import app.linksheet.feature.app.extension.activityDescriptor
import app.linksheet.testing.util.PackageInfoFake
import app.linksheet.testing.util.firstActivityResolveInfo
import fe.android.compose.icon.BitmapIconPainter
import fe.android.compose.icon.IconPainter

fun PackageInfoFake.toAppInfo(
    label: String = packageInfo.applicationInfo?.name!!,
    iconPainter: IconPainter = BitmapIconPainter.drawable(ImageFakes.EmptyDrawable),
): AppInfo {
    val appInfo = AppInfo(
        packageName = packageInfo.packageName,
        label = label,
        icon = iconPainter,
        flags = 0,
    )

    return appInfo
}

fun PackageInfoFake.toActivityAppInfo(
    label: String = packageInfo.applicationInfo?.name!!,
    icon: IconPainter = BitmapIconPainter.drawable(ImageFakes.EmptyDrawable),
): ActivityAppInfo {
    val appInfo = toAppInfo(label, icon)
    val componentInfo = firstActivityResolveInfo?.activityInfo!!
    return ActivityAppInfo(appInfo, componentInfo)
}

fun ResolveInfo.toActivityAppInfo(
    icon: IconPainter = BitmapIconPainter.drawable(ImageFakes.EmptyDrawable),
): ActivityAppInfo {
    val appInfo = AppInfo(
        packageName = activityInfo.packageName,
        label = activityInfo.name,
        icon = icon,
        flags = 0,
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
