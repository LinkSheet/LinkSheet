package fe.linksheet.module.preference.state

import fe.android.preference.helper.PreferenceDefinition

object AppStatePreferences : PreferenceDefinition() {
    val newDefaults_2024_11_29 = long("has_new_defaults_2024_11_29")
    val newDefaults_2024_11_30 = long("has_new_defaults_2024_11_30")

    val newDefaults_2024_12_16 = long("has_new_defaults_2024_12_16")
    val newDefaults_2024_12_16_InfoDismissed = boolean("has_new_defaults_2024_12_16_info_dismissed")

    val newDefaults_2024_12_29_InfoDismissed = boolean("has_new_defaults_2024_12_29_info_dismissed", true)
    val remoteConfigDialogDismissed = boolean("remote_config_dialog_dismissed", false)

    init {
        finalize()
    }
}
