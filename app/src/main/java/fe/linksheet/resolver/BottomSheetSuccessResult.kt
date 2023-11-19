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
        referrer: Uri?,
        val resolved: List<DisplayActivityInfo>,
        val filteredItem: DisplayActivityInfo?,
        val showExtended: Boolean,
        alwaysPreferred: Boolean?,
        hasSingleMatchingOption: Boolean = false,
        val resolveResults: Map<IntentResolver.Resolved, Result<ResolveType>?>,
        val libRedirectResult: LibRedirectResolver.LibRedirectResult? = null,
        val downloadable: Downloader.DownloadCheckResult = Downloader.DownloadCheckResult.NonDownloadable,
    ) : BottomSheetResult(uri) {
        private val totalCount = resolved.size + if (filteredItem != null) 1 else 0
        val isEmpty = totalCount == 0

        private val referringPackageName =
            if (referrer?.scheme == "android-app") referrer.host else null

        val isRegularPreferredApp = alwaysPreferred == true && filteredItem != null
        val app = filteredItem ?: resolved[0]

        val hasAutoLaunchApp =
            (isRegularPreferredApp || hasSingleMatchingOption) && (referringPackageName == null || app.packageName != referringPackageName)
    }

    class BottomSheetNoHandlersFound(uri: Uri?) : BottomSheetResult(uri)
}

