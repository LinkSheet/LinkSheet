package app.linksheet.feature.browser

import android.content.Intent

sealed class Browser(
    val displayName: String, val privateBrowser: Boolean,
    packageNames: Array<String>
) {
    val packageNames = setOf(*packageNames)

    abstract fun requestPrivateBrowsing(intent: Intent): Intent?
}

class Firefox(packageNames: Array<String>) : Browser("Firefox", true, packageNames) {
    companion object{
        private const val EXTRA = "private_browsing_mode"
    }
    override fun requestPrivateBrowsing(intent: Intent): Intent = intent.putExtra(EXTRA, true)
}

class Chromium(packageNames: Array<String>) : Browser("Chromium", false, packageNames) {
    override fun requestPrivateBrowsing(intent: Intent): Intent? {
        return null
    }
}

class Other(packageNames: Array<String>) : Browser("Other", false, packageNames) {
    override fun requestPrivateBrowsing(intent: Intent): Intent? {
        return null
    }
}


