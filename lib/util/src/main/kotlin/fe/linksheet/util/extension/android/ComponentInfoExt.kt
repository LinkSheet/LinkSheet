package fe.linksheet.util.extension.android

import android.content.ComponentName
import android.content.pm.ComponentInfo

val ComponentInfo.componentName: ComponentName
    get() = ComponentName(packageName, name)

