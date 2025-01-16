package app.linksheet.testing

import android.content.pm.ResolveInfo
import androidx.compose.ui.graphics.ImageBitmap
import fe.linksheet.module.app.ActivityAppInfo

object ResolveInfoFakes {
    val MiBrowser = buildResolveInfoTestFake { activity, application ->
        activity.name = "com.sec.android.app.sbrowser.SBrowserLauncherActivity"
        activity.packageName = "com.mi.globalbrowser"
        application.name = "MiBrowser"
    }

    val DuckDuckGoBrowser = buildResolveInfoTestFake { activity, application ->
        activity.name = "com.duckduckgo.app.dispatchers.IntentDispatcherActivity"
        activity.packageName = "com.duckduckgo.mobile.android"
        application.name = "DuckDuckGo"
    }

    val Youtube = buildResolveInfoTestFake { activity, application ->
        activity.name = "com.google.android.youtube.UrlActivity"
        activity.packageName = "com.google.android.youtube"
        application.name = "Youtube"
    }

    val NewPipe = buildResolveInfoTestFake { activity, application ->
        activity.name = "org.schabi.newpipe.RouterActivity"
        activity.packageName = "org.schabi.newpipe"
        application.name = "NewPipe"
    }

    val NewPipeEnhanced = buildResolveInfoTestFake { activity, application ->
        activity.name = "org.schabi.newpipe.RouterActivity"
        activity.packageName = "InfinityLoop1309.NewPipeEnhanced"
        application.name = "NewPipeEnhanced"
    }

    val Pepper = buildResolveInfoTestFake { activity, application ->
        activity.name = "com.pepper.presentation.dispatch.DispatchActivity"
        activity.packageName = "com.tippingcanoe.pepperpl"
        application.name = "Pepper"
    }

    val ChromeBrowser = buildResolveInfoTestFake { activity, application ->
        activity.name = "com.google.android.apps.chrome.Main"
        activity.packageName = "com.android.chrome"
        application.name = "Chrome"
    }

    val allApps = listOf(Youtube, NewPipe, NewPipeEnhanced, Pepper)
    val allBrowsers = listOf(MiBrowser, DuckDuckGoBrowser, ChromeBrowser)
    val allResolved = allApps + allBrowsers

    val ResolveInfo.packageName: String
        get() = activityInfo.packageName

    fun packageSetOf(vararg resolveInfos: ResolveInfo): Set<String> {
        return resolveInfos.mapTo(LinkedHashSet()) { it.packageName }
    }

    fun Iterable<ResolveInfo>.toKeyedMap(): Map<String, ResolveInfo> {
        return associateBy { it.packageName }
    }

    fun ResolveInfo.toKeyedMap(): Map<String, ResolveInfo> {
        return mapOf(this.packageName to this)
    }

    fun keyedMap(vararg resolveInfos: ResolveInfo): Map<String, ResolveInfo> {
        return listOf(*resolveInfos).toKeyedMap()
    }

    fun ResolveInfo.toAppInfo(label: String, icon: Lazy<ImageBitmap>): ActivityAppInfo {
        return ActivityAppInfo(activityInfo, label, icon)
    }

    fun ResolveInfo.toAppInfo(icon: Lazy<ImageBitmap>): ActivityAppInfo {
        return toAppInfo(activityInfo.applicationInfo.name, icon)
    }
}
