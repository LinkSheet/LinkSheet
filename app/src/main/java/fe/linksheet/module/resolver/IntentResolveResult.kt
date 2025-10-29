package fe.linksheet.module.resolver

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Stable
import app.linksheet.feature.downloader.DownloadCheckResult
import app.linksheet.feature.libredirect.LibRedirectResult
import fe.linksheet.feature.app.ActivityAppInfo
import fe.linksheet.util.intent.parser.UriException
import fe.std.uri.StdUrl
import me.saket.unfurl.UnfurlResult

sealed interface IntentResolveResult {
    data object Pending : IntentResolveResult

    data class WebSearch(
        val query: String, val newIntent: Intent, val resolvedList: List<ActivityAppInfo>,
    ) : IntentResolveResult

    class IntentResult(val intent: Intent) : IntentResolveResult
    class OtherProfile(val url: StdUrl) : IntentResolveResult

    @Stable
    class Default(
        val intent: Intent,
        val uri: Uri?,
        val unfurlResult: UnfurlResult?,
        referringPackageName: String?,
        val resolved: List<ActivityAppInfo>,
        val filteredItem: ActivityAppInfo?,
        alwaysPreferred: Boolean?,
        hasSingleMatchingOption: Boolean = false,
        val resolveModuleStatus: ResolveModuleStatus,
        val libRedirectResult: LibRedirectResult? = null,
        val downloadable: DownloadCheckResult? = DownloadCheckResult.NonDownloadable,
    ) : IntentResolveResult {
        private val totalCount = resolved.size + if (filteredItem != null) 1 else 0


        val isRegularPreferredApp = alwaysPreferred == true && filteredItem != null
        val app = filteredItem ?: resolved.firstOrNull()

        val hasAutoLaunchApp = (isRegularPreferredApp || hasSingleMatchingOption)
                && (referringPackageName == null || app?.packageName != referringPackageName)
    }

    data class IntentParseFailed(val exception: UriException) : IntentResolveResult
    data object UrlModificationFailed : IntentResolveResult
    data object ResolveUrlFailed : IntentResolveResult
    data object NoScenarioFound : IntentResolveResult
}
