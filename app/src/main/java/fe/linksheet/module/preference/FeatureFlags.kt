package fe.linksheet.module.preference

import fe.android.preference.helper.PreferenceDefinition

object FeatureFlags : PreferenceDefinition("feature_flag_new_bottom_sheet") {

    val linkSheetCompat = boolean("feature_flag_linksheet_compat")
    val urlPreview = boolean("feature_flag_url_preview")
    val declutterUrl = boolean("feature_flag_declutter_url")
    val experimentalUrlBar = boolean("experiment_url_bar")
    val parseShareText = boolean("experiment_share_parse_text", true)
    val allowCustomShareExtras = boolean("experiment_share_allow_custom_share_extras")
    val checkAllExtras = boolean("experiment_share_check_all_extras")


    init {
        finalize()
    }
}
