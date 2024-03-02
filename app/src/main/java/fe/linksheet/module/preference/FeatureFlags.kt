package fe.linksheet.module.preference

import fe.android.preference.helper.PreferenceDefinition

object FeatureFlags : PreferenceDefinition() {

    val featureFlagLinkSheetCompat = booleanPreference("feature_flag_linksheet_compat")
    val featureFlagNewBottomSheet = booleanPreference("feature_flag_new_bottom_sheet", true)
}
