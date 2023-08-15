package fe.linksheet.util

import android.content.Intent
import fe.linksheet.module.resolver.BrowserResolver

sealed class PrivateBrowsingBrowser(val displayName: String, vararg val packageName: String) {
    data object Firefox : PrivateBrowsingBrowser(
        "Firefox", *BrowserResolver.KnownBrowsers.Gecko.packageNames
    ) {
        private const val extra = "private_browsing_mode"

        override fun requestPrivateBrowsing(intent: Intent): Intent = intent.putExtra(extra, true)
    }

    abstract fun requestPrivateBrowsing(intent: Intent): Intent

    companion object {
        val supportedBrowsers = listOf(Firefox)

        fun getSupportedBrowser(packageName: String) = supportedBrowsers.find {
            packageName in it.packageName
        }
    }
}