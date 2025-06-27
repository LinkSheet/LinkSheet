package fe.linksheet.extension.android

import android.content.ComponentName
import android.content.pm.ActivityInfo
import fe.linksheet.feature.app.PackageIdHelper

@Deprecated(
    message = "Use extension value",
    replaceWith = ReplaceWith("this.componentName", "fe.linksheet.extension.android")
)
fun ActivityInfo.componentName() = ComponentName(applicationInfo.packageName, name)

infix fun ActivityInfo.isEqualTo(other: ActivityInfo) = componentName() == other.componentName()

val ActivityInfo.activityDescriptor
    get() = PackageIdHelper.getDescriptor(this)
