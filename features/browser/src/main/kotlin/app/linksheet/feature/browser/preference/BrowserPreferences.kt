package app.linksheet.feature.browser.preference

import app.linksheet.api.PreferenceRegistry
import fe.android.preference.helper.Preference

interface BrowserPreferences {
    val enable: Preference.Boolean
}

fun browserPreferences(registry: PreferenceRegistry): BrowserPreferences {
    return object : BrowserPreferences {
        override val enable = registry.boolean("enable_request_private_browsing_button", false)
    }
}
