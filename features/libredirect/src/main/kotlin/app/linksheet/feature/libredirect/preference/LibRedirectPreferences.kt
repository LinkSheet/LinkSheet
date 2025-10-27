package app.linksheet.feature.libredirect.preference

import fe.android.preference.helper.Preference

interface LibRedirectPreferences {
    val enable: Preference.Boolean
    val enableIgnoreLibRedirectButton: Preference.Boolean
}

fun libRedirectPreferences(boolean: (key: String, default: Boolean) -> Preference.Boolean): LibRedirectPreferences {
    return object : LibRedirectPreferences {
        override val enable = boolean("enable_lib_redirect", false)
        override val enableIgnoreLibRedirectButton = boolean("enable_ignore_lib_redirect_button", false)
    }
}
