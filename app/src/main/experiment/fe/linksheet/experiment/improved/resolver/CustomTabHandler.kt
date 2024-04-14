package fe.linksheet.experiment.improved.resolver

import androidx.browser.customtabs.CustomTabsIntent
import mozilla.components.support.utils.SafeIntent

object CustomTabHandler {
    private const val EXTRA_SESSION = CustomTabsIntent.EXTRA_SESSION
    private const val EXTRA_INDICATOR = "customtabs.extra"

    fun getInfo(intent: SafeIntent, allowCustomTab: Boolean): Pair<Boolean, List<String>?> {
        val isCustomTab = intent.hasExtra(EXTRA_SESSION)
        if (!isCustomTab || !allowCustomTab) return false to null

        val drop = intent.extras?.keySet()?.filter { extra -> !extra.contains(EXTRA_INDICATOR) } ?: emptyList()
        return true to drop
    }
}
