package fe.linksheet.experiment.improved.resolver

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Stable
import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.module.resolver.LibRedirectResult
import fe.linksheet.module.resolver.ResolveModuleStatus
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.resolver.DisplayActivityInfo
import me.saket.unfurl.UnfurlResult

sealed interface IntentResolveResult {
    data object Pending : IntentResolveResult

    data class WebSearch(
        val query: String, val newIntent: Intent, val resolvedList: List<DisplayActivityInfo>,
    ) : IntentResolveResult

    @Stable
    class Default(
        intent: Intent,
        uri: Uri?,
        val unfurlResult: UnfurlResult?,
        referrer: Uri?,
        resolved: List<DisplayActivityInfo>,
        val filteredItem: DisplayActivityInfo?,
        alwaysPreferred: Boolean?,
        hasSingleMatchingOption: Boolean = false,
        val resolveModuleStatus: ResolveModuleStatus,
        val libRedirectResult: LibRedirectResult? = null,
        val downloadable: DownloadCheckResult = DownloadCheckResult.NonDownloadable,
    ) : IntentResolveResult, BottomSheetResult.SuccessResult(uri, intent, resolved) {
        private val totalCount = resolved.size + if (filteredItem != null) 1 else 0

        private val referringPackageName = ReferrerHelper.getReferringPackage(referrer)

        val isRegularPreferredApp = alwaysPreferred == true && filteredItem != null
        val app = filteredItem ?: resolved.firstOrNull()

        val hasAutoLaunchApp = (isRegularPreferredApp || hasSingleMatchingOption)
                && (referringPackageName == null || app?.packageName != referringPackageName)

        override fun isEmpty(): Boolean {
            return totalCount == 0
        }
    }

    data object IntentParseFailed : IntentResolveResult
    data object UrlModificationFailed : IntentResolveResult
    data object ResolveUrlFailed : IntentResolveResult
}
