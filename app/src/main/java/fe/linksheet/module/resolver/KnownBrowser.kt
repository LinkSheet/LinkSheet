package fe.linksheet.module.resolver

import android.content.Intent

sealed class KnownBrowser(
    val displayName: String, val privateBrowser: Boolean, vararg packageNames: String
) {
    data object Gecko : KnownBrowser(
        "Firefox",
        true,
        "org.mozilla.fennec_fdroid",
        "us.spotco.fennec_dos",
        "io.github.forkmaintainers.iceraven",
        "org.mozilla.fenix.debug",
        "org.mozilla.fenix",
        "org.mozilla.firefox_beta",
        "org.mozilla.firefox",
        "org.mozilla.fennec_aurora",
        "org.mozilla.fennec",
        "org.mozilla.rocket",
        "org.mozilla.focus",
        "org.mozilla.focus.debug",
        "org.mozilla.focus.nightly",
        "org.mozilla.focus.beta",
        "org.torproject.torbrowser",
        "org.torproject.torbrowser_alpha",
        "org.mozilla.reference.browser",
        "info.guardianproject.orfox"
    ) {
        private const val EXTRA = "private_browsing_mode"
        override fun requestPrivateBrowsing(intent: Intent): Intent = intent.putExtra(EXTRA, true)
    }

    data object Chromium : KnownBrowser(
        "Chrome",
        false,
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
        "com.brave.browser_beta",
        "com.brave.browser_nightly",
        "com.microsoft.emmx",
        "com.ucmobile.intl",
        "com.uc.browser.en",
        "com.kiwibrowser.browser",
        "com.kiwibrowser.browser.dev",
        "com.vivaldi.browser",
        "com.vivaldi.browser.snapshot",
        "org.cromite.cromite",
        "us.spotco.mulch",
        "org.chromium.thorium",
        "com.sec.android.app.sbrowser",
        "com.sec.android.app.sbrowser.beta",
        "com.google.android.apps.chrome",
        "org.chromium.chrome"
    ) {
        override fun requestPrivateBrowsing(intent: Intent) = null
    }

    data object Other : KnownBrowser(
        "Other",
        false,
        "com.opera.browser",
        "com.opera.browser.beta",
        "com.opera.mini.native",
        "com.opera.mini.native.beta",
        "com.stoutner.privacybrowser.standard",
        "com.duckduckgo.mobile.android",
    ) {
        override fun requestPrivateBrowsing(intent: Intent) = null
    }

    private val packageNames = setOf(*packageNames)

    abstract fun requestPrivateBrowsing(intent: Intent): Intent?

    companion object {
        val browsers = setOf(Gecko, Chromium, Other)

        fun isKnownBrowser(packageName: String, privateOnly: Boolean = false): KnownBrowser? {
            val pkg = packageName.lowercase()
            return browsers.firstOrNull { pkg in it.packageNames && (it.privateBrowser || !privateOnly) }
        }
    }
}
