package fe.linksheet.util

import android.content.Intent

sealed class PrivateBrowsingBrowser(val displayName: String, vararg val packageName: String) {
    object Firefox : PrivateBrowsingBrowser(
        "Firefox",
        "org.mozilla.fennec_fdroid",
        "us.spotco.fennec_dos",
        "io.github.forkmaintainers.iceraven",
        "org.mozilla.fenix.debug",
        "org.mozilla.fenix",
        "org.mozilla.firefox_beta",
        "org.mozilla.firefox"
    ) {
        private const val extra = "private_browsing_mode"

        override fun requestPrivateBrowsing(intent: Intent) = intent.putExtra(extra, true)
    }

    abstract fun requestPrivateBrowsing(intent: Intent): Intent

    companion object {
        val supportedBrowsers = listOf(Firefox)

        fun getSupportedBrowser(packageName: String) = supportedBrowsers.find {
            packageName in it.packageName
        }
    }
}