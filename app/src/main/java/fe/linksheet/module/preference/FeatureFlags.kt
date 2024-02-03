package fe.linksheet.module.preference

import fe.android.preference.helper.Preferences

object FeatureFlags : Preferences() {

    @Deprecated("Feature flag is no longer used/required")
    val featureFlagShizuku = booleanPreference("feature_flag_shizuku")

    val featureFlagLinkSheetCompat = booleanPreference("feature_flag_linksheet_compat")
    val featureFlagNewBottomSheet = booleanPreference("feature_flag_new_bottom_sheet", true)
}
