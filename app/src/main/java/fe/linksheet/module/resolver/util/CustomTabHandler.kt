package fe.linksheet.module.resolver.util

import androidx.browser.customtabs.CustomTabsIntent
import mozilla.components.support.utils.SafeIntent

object CustomTabHandler {
    private const val EXTRA_SESSION = CustomTabsIntent.EXTRA_SESSION
    private const val EXTRA_INDICATOR = "customtabs.extra"

    fun getInfo(intent: SafeIntent, allowCustomTab: Boolean): CustomTabInfo {
        val isCustomTab = intent.hasExtra(EXTRA_SESSION)
        if (!isCustomTab || !allowCustomTab) return CustomTabInfo(false, null)

        val dropExtras = intent.extras
            ?.keySet()
            ?.filter { !it.contains(EXTRA_INDICATOR) }
            ?: emptyList()

        return CustomTabInfo(true, dropExtras)
    }
}

data class CustomTabInfo(val isCustomTab: Boolean, val extras: List<String>?)
