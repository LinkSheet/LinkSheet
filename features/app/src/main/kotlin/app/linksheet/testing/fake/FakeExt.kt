package app.linksheet.testing.fake

import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import app.linksheet.testing.util.PackageInfoFake
import app.linksheet.testing.util.firstActivityResolveInfo
import fe.android.compose.icon.BitmapIconPainter
import fe.android.compose.icon.IconPainter
import app.linksheet.feature.app.ActivityAppInfo
import app.linksheet.feature.app.DomainVerificationAppInfo
import app.linksheet.feature.app.LinkHandling
import app.linksheet.feature.app.PackageIdHelper

fun PackageInfoFake.toActivityAppInfo(
    label: String,
    icon: Drawable = ImageFakes.EmptyDrawable,
): ActivityAppInfo {
    return ActivityAppInfo(firstActivityResolveInfo?.activityInfo!!, label, BitmapIconPainter.drawable(icon))
}

fun PackageInfoFake.toActivityAppInfo(icon: Drawable = ImageFakes.EmptyDrawable): ActivityAppInfo {
    return toActivityAppInfo(packageInfo.applicationInfo!!.name, icon)
}

fun ResolveInfo.toActivityAppInfo(icon: Drawable = ImageFakes.EmptyDrawable): ActivityAppInfo {
    return ActivityAppInfo(activityInfo, activityInfo.name, BitmapIconPainter.drawable(icon))
}
fun PackageInfoFake.toDomainVerificationAppInfo(
    linkHandling: LinkHandling,
    stateNone: MutableList<String>,
    stateSelected: MutableList<String>,
    stateVerified: MutableList<String>,
    icon: IconPainter
): DomainVerificationAppInfo {
    return DomainVerificationAppInfo(
        packageInfo.applicationInfo!!.packageName,
        packageInfo.applicationInfo!!.name,
        icon,
        0,
        null,
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
    return DomainVerificationAppInfo(
        packageInfo.applicationInfo!!.packageName,
        packageInfo.applicationInfo!!.name,
        BitmapIconPainter.drawable(icon),
        0,
        null,
        linkHandling,
        stateNone,
        stateSelected,
        stateVerified,
    )
}

val ActivityInfo.activityDescriptor
    get() = PackageIdHelper.getDescriptor(this)

fun Iterable<ResolveInfo>.asDescriptors(): List<String> {
    return map { it.activityInfo.activityDescriptor }.distinct()
}

fun ActivityAppInfo.asDescriptor(): String {
    return PackageIdHelper.getDescriptor(this)
}
