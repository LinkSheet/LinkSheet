package app.linksheet.feature.shizuku.preference

import app.linksheet.api.PreferenceRegistry
import fe.android.preference.helper.Preference

interface ShizukuPreferences {
    val enable: Preference.Boolean
    val autoDisableLinkHandling: Preference.Boolean
    val managerPackage: Preference.Nullable<String>
    val managerCertSha256: Preference.Nullable<String>
}

fun shizukuPreferences(registry: PreferenceRegistry): ShizukuPreferences {
    return object : ShizukuPreferences {
        override val enable = registry.boolean("enable_shizuku", false)
        override val autoDisableLinkHandling = registry.boolean("auto_disable_link_handling", false)
        override val managerPackage = registry.string("shizuku_manager_package")
        override val managerCertSha256 = registry.string("shizuku_manager_cert_sha256")
    }
}
