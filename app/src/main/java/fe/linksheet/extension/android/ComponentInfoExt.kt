package fe.linksheet.extension.android

import android.content.ComponentName
import android.content.pm.ComponentInfo

val ComponentInfo.componentName: ComponentName
    get() = ComponentName(packageName, name)

