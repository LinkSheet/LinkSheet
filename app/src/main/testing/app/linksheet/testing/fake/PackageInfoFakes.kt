package app.linksheet.testing.fake

import app.linksheet.testing.util.buildPackageInfoTestFake

object PackageInfoFakes {
    val MiBrowser = buildPackageInfoTestFake("com.mi.globalbrowser", "MiBrowser") {
        activity("com.sec.android.app.sbrowser.SBrowserLauncherActivity") {
            addFilter {
                addAction("android.intent.action.VIEW")
                addCategory("android.intent.category.BROWSABLE")
                addCategory("android.intent.category.DEFAULT")
                addDataScheme("https")
                addDataScheme("http")
            }
        }
    }

    val DuckDuckGoBrowser = buildPackageInfoTestFake("com.duckduckgo.mobile.android", "DuckDuckGo") {
        activity("com.duckduckgo.app.dispatchers.IntentDispatcherActivity") {
            addFilter {
                addAction("android.intent.action.VIEW")
                addCategory("android.intent.category.BROWSABLE")
                addCategory("android.intent.category.DEFAULT")
                addDataScheme("https")
                addDataScheme("http")
                addDataScheme("duck")
            }

            addFilter {
                addAction("android.intent.action.VIEW")
                addCategory("android.intent.category.BROWSABLE")
                addCategory("android.intent.category.DEFAULT")
                addDataScheme("https")
                addDataScheme("http")
                addDataScheme("duck")
                addDataType("text/html")
                addDataType("text/plain")
                addDataType("application/xhtml+xml")
            }

            addFilter {
                addAction("android.intent.action.SEND")
                addCategory("android.intent.category.DEFAULT")
                addDataType("text/plain")
            }

            addFilter {
                addAction("android.nfc.action.NDEF_DISCOVERED")
                addCategory("android.intent.category.DEFAULT")
                addCategory("android.intent.category.BROWSABLE")
                addDataScheme("https")
                addDataScheme("http")
                addDataScheme("duck")
            }
        }
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
        activity("com.google.android.apps.chrome.Main") {
            addFilter {
                addAction("android.intent.action.VIEW")
                addCategory("android.intent.category.BROWSABLE")
                addCategory("android.intent.category.DEFAULT")
                addDataScheme("https")
                addDataScheme("http")
            }
        }
    }

    val Dummy = buildPackageInfoTestFake("dummy", "Dummy") {
        activity("dummy.Activity")
    }

    val allApps = listOf(Youtube, NewPipe, NewPipeEnhanced, Pepper, YatsePackageInfoFake)
    val allBrowsers = listOf(MiBrowser, DuckDuckGoBrowser, ChromeBrowser)
    val allResolved = allApps + allBrowsers
}
