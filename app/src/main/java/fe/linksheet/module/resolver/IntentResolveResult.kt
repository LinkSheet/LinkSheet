package fe.linksheet.module.resolver

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Stable
import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.module.resolver.util.ReferrerHelper
import me.saket.unfurl.UnfurlResult

sealed interface IntentResolveResult {
    data object Pending : IntentResolveResult

    data class WebSearch(
        val query: String, val newIntent: Intent, val resolvedList: List<DisplayActivityInfo>,
    ) : IntentResolveResult

    @Stable
    class Default(
        val intent: Intent,
        val uri: Uri?,
        val unfurlResult: UnfurlResult?,
        referrer: Uri?,
        val resolved: List<DisplayActivityInfo>,
        val filteredItem: DisplayActivityInfo?,
        alwaysPreferred: Boolean?,
        hasSingleMatchingOption: Boolean = false,
        val resolveModuleStatus: ResolveModuleStatus,
        val libRedirectResult: LibRedirectResult? = null,
        val downloadable: DownloadCheckResult = DownloadCheckResult.NonDownloadable,
    ) : IntentResolveResult {
        private val totalCount = resolved.size + if (filteredItem != null) 1 else 0

        private val referringPackageName = ReferrerHelper.getReferringPackage(referrer)

        val isRegularPreferredApp = alwaysPreferred == true && filteredItem != null
        val app = filteredItem ?: resolved.firstOrNull()

        val hasAutoLaunchApp = (isRegularPreferredApp || hasSingleMatchingOption)
                && (referringPackageName == null || app?.packageName != referringPackageName)
    }

    data object IntentParseFailed : IntentResolveResult
    data object UrlModificationFailed : IntentResolveResult
    data object ResolveUrlFailed : IntentResolveResult
}
