package fe.linksheet.module.preference.state

import fe.android.preference.helper.PreferenceDefinition

object AppStatePreferences : PreferenceDefinition() {
    val newDefaults_2024_12_16_InfoDismissed = boolean("has_new_defaults_2024_12_16_info_dismissed", true)
    val newDefaults_2024_12_29_InfoDismissed = boolean("has_new_defaults_2024_12_29_info_dismissed", true)
    val newDefaults_2025_12_15_InfoDismissed = boolean("has_new_defaults_2025_12_15_info_dismissed")

    val remoteConfigDialogDismissed = boolean("remote_config_dialog_dismissed", false)

    @Suppress("ObjectPropertyName")
    object NewDefaults {
        val `2024-11-29` = long("has_new_defaults_2024_11_29")
        val `2024-11-30` = long("has_new_defaults_2024_11_30")
        val `2024-12-16` = long("has_new_defaults_2024_12_16")
        val `2025-07-29` = long("has_new_defaults_2025_07_29")
        val `2025-08-03` = long("has_new_defaults_2025_08_03")
        val `2025-12-15` = long("has_new_defaults_2025_12_15")
    }

    val newDefaults = NewDefaults

    init {
        finalize()
    }
}
