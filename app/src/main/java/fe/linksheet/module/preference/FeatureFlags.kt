package fe.linksheet.module.preference

import fe.android.preference.helper.PreferenceDefinition

object FeatureFlags : PreferenceDefinition("feature_flag_new_bottom_sheet", "feature_flag_declutter_url", "feature_flag_url_preview") {

    val linkSheetCompat = boolean("feature_flag_linksheet_compat")

    val experimentalUrlBar = boolean("experiment_url_bar")
    val urlPreview = boolean("experiment_url_bar_preview")
    val declutterUrl = boolean("experiment_url_bar_declutter_url")

    val parseShareText = boolean("experiment_share_parse_text", true)
    val allowCustomShareExtras = boolean("experiment_share_allow_custom_share_extras")
    val checkAllExtras = boolean("experiment_share_check_all_extras")

    init {
        finalize()
    }
}
