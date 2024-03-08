package fe.linksheet.module.preference

import fe.android.preference.helper.PreferenceDefinition

object FeatureFlags : PreferenceDefinition("feature_flag_new_bottom_sheet") {

    val featureFlagLinkSheetCompat = boolean("feature_flag_linksheet_compat")

    init {
        finalize()
    }
}
