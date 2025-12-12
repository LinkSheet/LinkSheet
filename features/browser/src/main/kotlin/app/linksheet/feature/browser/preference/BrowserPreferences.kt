package app.linksheet.feature.browser.preference

import fe.android.preference.helper.Preference

interface BrowserPreferences {
    val enable: Preference.Boolean
}

fun browserPreferences(boolean: (key: String, default: Boolean) -> Preference.Boolean): BrowserPreferences {
    return object : BrowserPreferences {
        override val enable = boolean("enable_request_private_browsing_button", false)
    }
}
