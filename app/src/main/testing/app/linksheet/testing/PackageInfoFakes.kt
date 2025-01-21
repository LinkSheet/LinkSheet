package app.linksheet.testing


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
        activity("com.pepper.presentation.dispatch.DispatchActivity")
    }

    val ChromeBrowser = buildPackageInfoTestFake("com.android.chrome", "Chrome") {
        activity("com.google.android.apps.chrome.Main")
    }

    val allApps = listOf(Youtube, NewPipe, NewPipeEnhanced, Pepper)
    val allBrowsers = listOf(MiBrowser, DuckDuckGoBrowser, ChromeBrowser)
    val allResolved = allApps + allBrowsers
}
