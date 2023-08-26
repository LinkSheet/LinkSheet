package fe.linksheet.resolver

import android.content.Intent
import android.net.Uri
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.resolver.IntentResolver
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.resolver.urlresolver.ResolveType


sealed class BottomSheetResult(val uri: Uri?) {
    class BottomSheetSuccessResult(
        val intent: Intent,
        uri: Uri?,
        val resolved: List<DisplayActivityInfo>,
        val filteredItem: DisplayActivityInfo?,
        val showExtended: Boolean,
        private val alwaysPreferred: Boolean?,
        val setByInterconnect: Boolean?,
        private val hasSingleMatchingOption: Boolean = false,
        val resolveResults: Map<IntentResolver.Resolved, Result<ResolveType>?>,
        val libRedirectResult: LibRedirectResolver.LibRedirectResult? = null,
        val downloadable: Downloader.DownloadCheckResult = Downloader.DownloadCheckResult.NonDownloadable,
    ) : BottomSheetResult(uri) {
        val totalCount = resolved.size + if (filteredItem != null) 1 else 0
        val isEmpty = totalCount == 0

        val isRegularPreferredApp = setByInterconnect == true || (alwaysPreferred == true && filteredItem != null)

        val hasAutoLaunchApp = isRegularPreferredApp || hasSingleMatchingOption

        val app = filteredItem ?: resolved[0]
    }

    class BottomSheetNoHandlersFound(uri: Uri?) : BottomSheetResult(uri)
}

