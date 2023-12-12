package fe.linksheet.module.preference

import fe.android.preference.helper.Preferences

object FeatureFlags : Preferences() {
    val featureFlagShizuku = booleanPreference("feature_flag_shizuku")
    val featureFlagLinkSheetCompat = booleanPreference("feature_flag_linksheet_compat")
}