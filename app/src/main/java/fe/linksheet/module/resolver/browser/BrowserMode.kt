package fe.linksheet.module.resolver.browser

import fe.android.preference.helper.OptionTypeMapper

sealed class BrowserMode(val value: String) {
    data object None : BrowserMode("none")
    data object AlwaysAsk : BrowserMode("always_ask")
    data object SelectedBrowser : BrowserMode("browser")
    data object Whitelisted : BrowserMode("whitelisted")

    companion object : OptionTypeMapper<BrowserMode, String>(
        { it.value }, { arrayOf(None, AlwaysAsk, SelectedBrowser, Whitelisted) }
    )

    override fun toString(): String {
        return value
    }
}
