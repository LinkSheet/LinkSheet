package fe.linksheet.module.preference.state

import app.linksheet.feature.remoteconfig.preference.remoteConfigStatePreferences
import fe.linksheet.module.preference.LinkSheetPreferenceDefinition

object AppStatePreferences : LinkSheetPreferenceDefinition() {
    val lastClDismissed = int("last_cl_dismissed", -1)
    val newDefaults_2024_12_16_InfoDismissed = boolean("has_new_defaults_2024_12_16_info_dismissed", true)
    val newDefaults_2024_12_29_InfoDismissed = boolean("has_new_defaults_2024_12_29_info_dismissed", true)
    val newDefaults_2025_12_15_InfoDismissed = boolean("has_new_defaults_2025_12_15_info_dismissed")

    val remoteConfig = remoteConfigStatePreferences(registry)

    @Suppress("ObjectPropertyName")
    object NewDefaults {
        val `2024-11-29` = long("has_new_defaults_2024_11_29")
        val `2024-11-30` = long("has_new_defaults_2024_11_30")
        val `2024-12-16` = long("has_new_defaults_2024_12_16")
        val `2025-07-29` = long("has_new_defaults_2025_07_29")
        val `2025-08-03` = long("has_new_defaults_2025_08_03")
        val `2025-12-15` = long("has_new_defaults_2025_12_15")
        val `2026-04-27` = long("has_new_defaults_2026_04_27")
    }

    val newDefaults = NewDefaults

    init {
        finalize()
    }
}
