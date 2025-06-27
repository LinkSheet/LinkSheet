package app.linksheet.testing.fake

import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import androidx.compose.ui.graphics.ImageBitmap
import app.linksheet.testing.util.PackageInfoFake
import app.linksheet.testing.util.firstActivityResolveInfo
import fe.android.compose.icon.BitmapIconPainter
import fe.linksheet.feature.app.ActivityAppInfo
import fe.linksheet.feature.app.DomainVerificationAppInfo
import fe.linksheet.feature.app.LinkHandling
import fe.linksheet.feature.app.PackageIdHelper

fun PackageInfoFake.toActivityAppInfo(
    label: String,
    icon: Lazy<ImageBitmap> = ImageFakes.ImageBitmap,
): ActivityAppInfo {
    return ActivityAppInfo(firstActivityResolveInfo?.activityInfo!!, label, BitmapIconPainter.bitmap(icon.value))
}

fun PackageInfoFake.toActivityAppInfo(icon: Lazy<ImageBitmap> = ImageFakes.ImageBitmap): ActivityAppInfo {
    return toActivityAppInfo(packageInfo.applicationInfo!!.name, icon)
}

fun ResolveInfo.toActivityAppInfo(icon: Lazy<ImageBitmap> = ImageFakes.ImageBitmap): ActivityAppInfo {
    return ActivityAppInfo(activityInfo, activityInfo.name, BitmapIconPainter.bitmap(icon.value))
}

fun PackageInfoFake.toDomainVerificationAppInfo(
    linkHandling: LinkHandling,
    stateNone: MutableList<String>,
    stateSelected: MutableList<String>,
    stateVerified: MutableList<String>,
    icon: Lazy<ImageBitmap> = ImageFakes.ImageBitmap,
): DomainVerificationAppInfo {
    return DomainVerificationAppInfo(
        packageInfo.applicationInfo!!.packageName,
        packageInfo.applicationInfo!!.name,
        BitmapIconPainter.bitmap(icon.value),
        0,
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
