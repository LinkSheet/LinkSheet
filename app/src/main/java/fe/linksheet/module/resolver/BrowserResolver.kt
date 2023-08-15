package fe.linksheet.module.resolver

import android.app.Application
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import fe.linksheet.extension.android.queryResolveInfosByIntent
import fe.linksheet.extension.android.toDisplayActivityInfos
import fe.linksheet.extension.android.toPackageKeyedMap

class BrowserResolver(val context: Application) {
    enum class KnownBrowsers(vararg val packageNames: String) {
        Gecko(
            "org.mozilla.fennec_fdroid",
            "us.spotco.fennec_dos",
            "io.github.forkmaintainers.iceraven",
            "org.mozilla.fenix.debug",
            "org.mozilla.fenix",
            "org.mozilla.firefox_beta",
            "org.mozilla.firefox"
        ),
        Chromium(
            "com.android.chrome",
            "com.chrome.canary",
            "com.chrome.beta",
            "com.chrome.dev",
            "com.sec.android.app.sbrowser",
            "com.opera.browser",
            "com.opera.browser.beta",
            "com.opera.cryptobrowser",
            "com.opera.gx",
            "com.brave.browser",
            "com.microsoft.emmx",
            "com.UCMobile.intl"
        ),
        Other("com.opera.mini.native", "com.opera.mini.native.beta");

        companion object {
            fun isKnownBrowser(packageName: String): KnownBrowsers? {
                return values().firstOrNull { packageName in it.packageNames }
            }
        }
    }

    companion object {
        private val httpSchemeUri: Uri = Uri.fromParts("http", "", "")
        private val httpsSchemeUri: Uri = Uri.fromParts("https", "", "")

        private val baseBrowserIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)

        val httpBrowserIntent = Intent(baseBrowserIntent).setData(httpSchemeUri)
        val httpsBrowserIntent = Intent(baseBrowserIntent).setData(httpsSchemeUri)

        fun isSchemeTypicallySupportedByBrowsers(intent: Intent): Boolean {
            return httpSchemeUri.scheme == intent.scheme || httpsSchemeUri.scheme == intent.scheme
        }
    }

    fun queryDisplayActivityInfoBrowsers(sorted: Boolean) = queryBrowsers()
        .toDisplayActivityInfos(context, sorted)

    fun queryBrowsers(): Map<String, ResolveInfo> {
        // Some apps (looking at you Coinbase) declare their app as a browser (via CATEGORY_BROWSABLE and ACTION_VIEW) but ONLY for HTTPS schemes.
        // Previously, LinkSheet assumed (rightfully so) that an app, which is declared as a BROWSER, could handle HTTP (alongside HTTPS and other schemes).
        // Since this appears to NOT be the case, we have to send two intent queries (one for HTTP and one for HTTPS), then de-dupe them using a map
        return queryBrowsers(httpBrowserIntent) + queryBrowsers(httpsBrowserIntent)
    }

    private fun queryBrowsers(intent: Intent): Map<String, ResolveInfo> {
        return context.packageManager.queryResolveInfosByIntent(intent, true).toPackageKeyedMap()
    }
}


