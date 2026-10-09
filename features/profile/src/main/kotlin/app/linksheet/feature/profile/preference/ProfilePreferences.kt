package app.linksheet.feature.profile.preference

import app.linksheet.api.PreferenceRegistry
import fe.android.preference.helper.Preference

interface ProfilePreferences {
    val enable: Preference.Boolean
    val sendTarget: Preference.Boolean
}

fun profilePreferences(registry: PreferenceRegistry): ProfilePreferences {
    return object : ProfilePreferences {
        override val enable = registry.boolean("bottom_sheet_profile_switcher", false)
        override val sendTarget = registry.boolean("send_target", false)
    }
}
