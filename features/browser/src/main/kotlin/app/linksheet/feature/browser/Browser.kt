package app.linksheet.feature.browser

import android.content.Intent

sealed class Browser(
    val privateBrowser: Boolean,
    packageNames: Array<String>
) {
    val packageNames = setOf(*packageNames)

    abstract fun requestPrivateBrowsing(intent: Intent): Intent?
}

class FirefoxPrivateBrowser(packageNames: Array<String>) : Browser(true, packageNames) {
    companion object {
        private const val EXTRA = "private_browsing_mode"
    }

    override fun requestPrivateBrowsing(intent: Intent): Intent {
        return intent.putExtra(EXTRA, true)
    }
}

class OtherBrowser(packageNames: Array<String>) : Browser(false, packageNames) {
    override fun requestPrivateBrowsing(intent: Intent): Intent? {
        return null
    }
}


