package fe.linksheet.module.resolver.util

import androidx.browser.customtabs.CustomTabsIntent
import app.linksheet.mozilla.components.support.utils.SafeIntent

object CustomTabHandler {
    private val CUSTOM_TAB_EXTRAS_PREFIXES = arrayOf(
        "android.support.customtabs.extra.",
        "androidx.browser.customtabs.extra.",
        "com.android.browser.",
        "org.chromium.chrome.browser.customtabs.",
    )

    fun getInfo(intent: SafeIntent, allowCustomTab: Boolean): CustomTabInfo {
        return when(val info = getInfo2(intent, allowCustomTab)) {
            is CustomTabInfo2.Allowed -> CustomTabInfo(true, info.dropExtras)
            // Not strictly correct as isCustomTab should be true even if we don't allow custom tabs; This is addressed by getInfo2
            CustomTabInfo2.NotAllowed -> CustomTabInfo(false, null)
            CustomTabInfo2.NotApplicable -> CustomTabInfo(false, null)
        }
    }

    fun getInfo2(intent: SafeIntent, allowCustomTab: Boolean): CustomTabInfo2 {
        val isCustomTab = intent.hasExtra(CustomTabsIntent.EXTRA_SESSION)
        if (!isCustomTab) {
            return CustomTabInfo2.NotApplicable
        }

        if (!allowCustomTab) {
            return CustomTabInfo2.NotAllowed
        }

        val dropExtras = intent.extras
            ?.keySet()
            ?.filter { extra -> !CUSTOM_TAB_EXTRAS_PREFIXES.any { extra.startsWith(it) } }
            ?: emptyList()

        return CustomTabInfo2.Allowed(dropExtras)
    }
}

data class CustomTabInfo(val isCustomTab: Boolean, val dropExtras: List<String>?)

sealed interface CustomTabInfo2 {
    val dropExtras: List<String>
        get() = emptyList()

    data object NotApplicable : CustomTabInfo2
    data object NotAllowed : CustomTabInfo2
    data class Allowed(override val dropExtras: List<String> ) : CustomTabInfo2
}
