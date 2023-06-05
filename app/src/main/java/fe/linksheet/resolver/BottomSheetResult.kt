package fe.linksheet.resolver

import android.content.Intent
import android.net.Uri
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.resolver.RedirectFollower

data class BottomSheetResult(
    val intent: Intent,
    val uri: Uri?,
    val resolved: List<DisplayActivityInfo>,
    val filteredItem: DisplayActivityInfo?,
    val showExtended: Boolean,
    private val alwaysPreferred: Boolean?,
    private val hasSingleMatchingOption: Boolean = false,
    val followRedirect: Result<RedirectFollower.FollowRedirect>? = null,
    val libRedirectResult: LibRedirectResolver.LibRedirectResult? = null,
    val downloadable: Downloader.DownloadCheckResult = Downloader.DownloadCheckResult.NonDownloadable,
) {
    val totalCount = resolved.size + if (filteredItem != null) 1 else 0
    val isEmpty = totalCount == 0

    val isRegularPreferredApp = alwaysPreferred == true && filteredItem != null

    val hasAutoLaunchApp = isRegularPreferredApp || hasSingleMatchingOption

    val app = filteredItem ?: resolved[0]
}