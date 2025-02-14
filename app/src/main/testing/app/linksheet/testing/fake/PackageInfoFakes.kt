package app.linksheet.testing.fake

import app.linksheet.testing.util.buildPackageInfoTestFake

object PackageInfoFakes {
    val MiBrowser = buildPackageInfoTestFake("com.mi.globalbrowser", "MiBrowser") {
        activity("com.sec.android.app.sbrowser.SBrowserLauncherActivity")
    }

    val DuckDuckGoBrowser = buildPackageInfoTestFake("com.duckduckgo.mobile.android", "DuckDuckGo") {
        activity("com.duckduckgo.app.dispatchers.IntentDispatcherActivity")
    }

    val Youtube = buildPackageInfoTestFake("com.google.android.youtube", "Youtube") {
        activity("com.google.android.youtube.UrlActivity")
    }

    val NewPipe = buildPackageInfoTestFake("org.schabi.newpipe", "NewPipe") {
        activity("org.schabi.newpipe.RouterActivity")
    }

    val NewPipeEnhanced = buildPackageInfoTestFake("InfinityLoop1309.NewPipeEnhanced", "NewPipeEnhanced") {
        activity("org.schabi.newpipe.RouterActivity")
    }

    val Pepper = buildPackageInfoTestFake("com.tippingcanoe.pepperpl", "Pepper") {
        activity("com.pepper.presentation.dispatch.DispatchActivity") {
            addFilter {
                addAction("android.intent.action.VIEW")
                addCategory("android.intent.category.DEFAULT")
                addCategory("android.intent.category.BROWSABLE")
                addDataScheme("https")
                addDataAuthority("www.pepper.pl", null)
                addDataAuthority("pl.dea.ls", null)
            }
        }
    }

    val ChromeBrowser = buildPackageInfoTestFake("com.android.chrome", "Chrome") {
        activity("com.google.android.apps.chrome.Main")
    }

    val allApps = listOf(Youtube, NewPipe, NewPipeEnhanced, Pepper, YatsePackageInfoFake)
    val allBrowsers = listOf(MiBrowser, DuckDuckGoBrowser, ChromeBrowser)
    val allResolved = allApps + allBrowsers
}
