package fe.linksheet.module.preference

import fe.android.preference.helper.PreferenceDefinition

object FeatureFlags : PreferenceDefinition("feature_flag_new_bottom_sheet") {

    val linkSheetCompat = boolean("feature_flag_linksheet_compat")
    val urlPreview = boolean("feature_flag_url_preview")
    val declutterUrl = boolean("feature_flag_declutter_url")

    init {
        finalize()
    }
}
