package app.linksheet.feature.browser

import android.content.Context

fun PrivateBrowsingService(context: Context): PrivateBrowsingService {
    val browsers = arrayOf(
        FirefoxPrivateBrowser(context.resources.getStringArray(R.array.firefox)),
        OtherBrowser(context.resources.getStringArray(R.array.chromium)),
        OtherBrowser(context.resources.getStringArray(R.array.other))
    )

    return PrivateBrowsingService(browsers = browsers)
}

class PrivateBrowsingService internal constructor(
    private val browsers: Array<Browser>
) {
    fun isKnownBrowser(packageName: String?, privateOnly: Boolean = false): Browser? {
        if (packageName == null) return null

        val pkg = packageName.lowercase()
        return browsers.firstOrNull { pkg in it.packageNames && (it.privateBrowser || !privateOnly) }
    }
}
