package fe.linksheet.resolver.util

import android.content.pm.ResolveInfo

object ResolveInfos {
    val miBrowser = buildResolveInfoTestMock { activity, application ->
        activity.name = "com.sec.android.app.sbrowser.SBrowserLauncherActivity"
        activity.packageName = "com.mi.globalbrowser"
    }

    val duckduckgoBrowser = buildResolveInfoTestMock { activity, application ->
        activity.name = "com.duckduckgo.app.dispatchers.IntentDispatcherActivity"
        activity.packageName = "com.duckduckgo.mobile.android"
    }

    val youtube = buildResolveInfoTestMock { activity, application ->
        activity.name = "com.google.android.youtube.UrlActivity"
        activity.packageName = "com.google.android.youtube"
    }

    val newPipe = buildResolveInfoTestMock { activity, application ->
        activity.name = "org.schabi.newpipe.RouterActivity"
        activity.packageName = "org.schabi.newpipe"
    }

    val newPipeEnhanced = buildResolveInfoTestMock { activity, application ->
        activity.name = "org.schabi.newpipe.RouterActivity"
        activity.packageName = "InfinityLoop1309.NewPipeEnhanced"
    }

    val pepper = buildResolveInfoTestMock { activity, application ->
        activity.name = "com.pepper.presentation.dispatch.DispatchActivity"
        activity.packageName = "com.tippingcanoe.pepperpl"
    }

    val allApps = listOf(youtube, newPipe, newPipeEnhanced)
    val allBrowsers = listOf(miBrowser, duckduckgoBrowser)
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
}
