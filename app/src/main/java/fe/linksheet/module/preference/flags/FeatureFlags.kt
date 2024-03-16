package fe.linksheet.module.preference.flags

import fe.android.preference.helper.PreferenceDefinition

object FeatureFlags : PreferenceDefinition(
    "feature_flag_new_bottom_sheet",
    "feature_flag_declutter_url",
    "feature_flag_url_preview",
    "experiment_share_parse_text"
) {
    val linkSheetCompat = boolean("feature_flag_linksheet_compat")
    val parseShareText = boolean("feature_flag_share_parse_text", true)

    init { finalize() }
}



