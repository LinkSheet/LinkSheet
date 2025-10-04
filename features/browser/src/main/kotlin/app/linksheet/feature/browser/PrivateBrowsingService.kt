package app.linksheet.feature.browser

import android.content.Context

fun PrivateBrowsingService(context: Context): PrivateBrowsingService {
    val browsers = arrayOf(
        Firefox(context.resources.getStringArray(R.array.firefox)),
        Chromium(context.resources.getStringArray(R.array.chromium)),
        Other(context.resources.getStringArray(R.array.other))
    )

    return PrivateBrowsingService(browsers = browsers)
}

class PrivateBrowsingService internal constructor(
    val browsers: Array<Browser>
) {
    fun isKnownBrowser(packageName: String?, privateOnly: Boolean = false): Browser? {
        if (packageName == null) return null

        val pkg = packageName.lowercase()
        return browsers.firstOrNull { pkg in it.packageNames && (it.privateBrowser || !privateOnly) }
    }
}
