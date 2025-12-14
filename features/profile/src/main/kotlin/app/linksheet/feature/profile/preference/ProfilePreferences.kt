package app.linksheet.feature.profile.preference

import fe.android.preference.helper.Preference

interface ProfilePreferences {
    val enable: Preference.Boolean
}

fun profilePreferences(boolean: (key: String, default: Boolean) -> Preference.Boolean): ProfilePreferences {
    return object : ProfilePreferences {
        override val enable = boolean("bottom_sheet_profile_switcher", false)
    }
}
