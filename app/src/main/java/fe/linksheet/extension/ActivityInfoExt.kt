package fe.linksheet.extension

import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import com.tasomaniac.openwith.resolver.IconLoader

fun ActivityInfo.componentName() = ComponentName(applicationInfo.packageName, name)

infix fun ActivityInfo.isEqualTo(other: ActivityInfo) = componentName() == other.componentName()

fun ActivityInfo.getIcon(context: Context): Drawable? {
    IconLoader.getIcon(packageName, icon)?.let { return it }
    IconLoader.getIcon(packageName, iconResource)?.let { return it }

    return loadIcon(context.packageManager)
}