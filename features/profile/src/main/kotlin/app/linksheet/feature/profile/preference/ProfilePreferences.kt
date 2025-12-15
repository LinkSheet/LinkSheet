package app.linksheet.feature.profile.preference

import app.linksheet.api.PreferenceRegistry
import fe.android.preference.helper.Preference

interface ProfilePreferences {
    val enable: Preference.Boolean
}

fun profilePreferences(registry: PreferenceRegistry): ProfilePreferences {
    return object : ProfilePreferences {
        override val enable = registry.boolean("bottom_sheet_profile_switcher", false)
    }
}
