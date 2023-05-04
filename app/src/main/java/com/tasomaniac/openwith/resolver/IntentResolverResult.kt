package com.tasomaniac.openwith.resolver

import android.net.Uri
import fe.linksheet.activity.bottomsheet.BottomSheetViewModel
import fe.linksheet.module.downloader.Downloader

data class IntentResolverResult(
    val uri: Uri?,
    val resolved: List<DisplayActivityInfo>,
    val filteredItem: DisplayActivityInfo?,
    val showExtended: Boolean,
    private val alwaysPreferred: Boolean?,
    private val hasSingleMatchingOption: Boolean = false,
    val followRedirect: BottomSheetViewModel.FollowRedirect? = null,
    val downloadable: Downloader.DownloadCheckResult = Downloader.DownloadCheckResult.NonDownloadable,
) {
    val totalCount = resolved.size + if (filteredItem != null) 1 else 0
    val isEmpty = totalCount == 0

    val isRegularPreferredApp = alwaysPreferred == true && filteredItem != null

    val hasAutoLaunchApp = isRegularPreferredApp || hasSingleMatchingOption

    val app = filteredItem ?: resolved[0]
}