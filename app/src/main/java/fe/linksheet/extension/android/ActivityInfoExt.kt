package fe.linksheet.extension.android

import android.content.ComponentName
import android.content.pm.ActivityInfo

fun ActivityInfo.componentName() = ComponentName(applicationInfo.packageName, name)

infix fun ActivityInfo.isEqualTo(other: ActivityInfo) = componentName() == other.componentName()
