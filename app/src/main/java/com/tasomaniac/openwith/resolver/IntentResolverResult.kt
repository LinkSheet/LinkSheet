package com.tasomaniac.openwith.resolver

import android.net.Uri
import fe.linksheet.activity.bottomsheet.BottomSheetViewModel

data class IntentResolverResult(
    val uri: Uri?,
    val resolved: List<DisplayActivityInfo>,
    val filteredItem: DisplayActivityInfo?,
    val showExtended: Boolean,
    val alwaysPreferred: Boolean?,
    val hasSingleMatchingOption: Boolean = false,
    val followRedirect: BottomSheetViewModel.FollowRedirect? = null,
) {
    val isEmpty get() = totalCount() == 0

    fun totalCount() = resolved.size + if (filteredItem != null) 1 else 0
}
