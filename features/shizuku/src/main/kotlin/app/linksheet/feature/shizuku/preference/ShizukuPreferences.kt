package app.linksheet.feature.shizuku.preference

import app.linksheet.api.PreferenceRegistry
import fe.android.preference.helper.Preference

interface ShizukuPreferences {
    val enable: Preference.Boolean
    val autoDisableLinkHandling: Preference.Boolean
}

fun shizukuPreferences(registry: PreferenceRegistry): ShizukuPreferences {
    return object : ShizukuPreferences {
        override val enable = registry.boolean("enable_shizuku", false)
        override val autoDisableLinkHandling = registry.boolean("auto_disable_link_handling", false)
    }
}
