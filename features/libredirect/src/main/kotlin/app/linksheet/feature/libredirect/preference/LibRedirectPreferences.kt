package app.linksheet.feature.libredirect.preference

import app.linksheet.api.PreferenceRegistry
import fe.android.preference.helper.Preference

enum class Experiment {
    CustomInstances
}

interface LibRedirectPreferences {
    val enable: Preference.Boolean
    val enableIgnoreLibRedirectButton: Preference.Boolean
}

fun libRedirectPreferences(registry: PreferenceRegistry): LibRedirectPreferences {
    return object : LibRedirectPreferences {
        override val enable = registry.boolean("enable_lib_redirect", false)
        override val enableIgnoreLibRedirectButton = registry.boolean("enable_ignore_lib_redirect_button", false)
    }
}
