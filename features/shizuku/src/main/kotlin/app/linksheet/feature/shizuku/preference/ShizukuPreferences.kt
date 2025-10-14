package app.linksheet.feature.shizuku.preference

import fe.android.preference.helper.Preference

interface ShizukuPreferences {
    val enable: Preference.Boolean
}

fun shizukuPreferences(boolean: (key: String, default: Boolean) -> Preference.Boolean): ShizukuPreferences {
    return object  : ShizukuPreferences {
        override val enable = boolean("enable_shizuku", false)
    }
}
