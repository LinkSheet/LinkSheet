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

    fun getInfo2(intent: SafeIntent, allowCustomTab: Boolean): CustomTabInfo2 {
        val isCustomTab = intent.hasExtra(EXTRA_SESSION)
        if (!isCustomTab) {
            return CustomTabInfo2.NotApplicable
        }

        if (!allowCustomTab) {
            return CustomTabInfo2.NotAllowed
        }

        val dropExtras = intent.extras
            ?.keySet()
            ?.filter { extra -> !extra.contains(EXTRA_INDICATOR) }
            ?: emptyList()

        return CustomTabInfo2.Allowed(dropExtras)
    }
}

data class CustomTabInfo(val isCustomTab: Boolean, val extras: List<String>?)

sealed interface CustomTabInfo2 {
    val dropExtras: List<String>
        get() = emptyList()

    data object NotApplicable : CustomTabInfo2
    data object NotAllowed : CustomTabInfo2
    data class Allowed(override val dropExtras: List<String> ) : CustomTabInfo2
}
