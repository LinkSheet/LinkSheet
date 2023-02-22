package fe.linksheet.extension

import android.content.Context
import android.content.pm.ResolveInfo
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import com.tasomaniac.openwith.resolver.IconLoader

fun ResolveInfo.toDisplayActivityInfo(context: Context): DisplayActivityInfo {
    return DisplayActivityInfo(
        activityInfo = this.activityInfo,
        displayLabel = this.loadLabel(context.packageManager).toString()
    ).apply {
        displayIcon = IconLoader.loadFor(context, this.activityInfo)
    }
}