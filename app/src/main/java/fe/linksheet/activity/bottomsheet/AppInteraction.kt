package fe.linksheet.activity.bottomsheet

import android.content.Intent
import app.linksheet.feature.app.core.ActivityAppInfo
import app.linksheet.feature.downloader.DownloadCheckResult
import app.linksheet.feature.libredirect.LibRedirectResult
import app.linksheet.feature.profile.core.CrossProfile

sealed interface BottomSheetInteraction {

}

data class ManualRedirectInteraction(val uri: String) : BottomSheetInteraction
data class IgnoreLibRedirectInteraction(val result: LibRedirectResult.Redirected) : BottomSheetInteraction
data class StartDownloadInteraction(
    val url: String,
    val downloadable: DownloadCheckResult.Downloadable
) : BottomSheetInteraction

data class CopyUrlInteraction(val url: String) : BottomSheetInteraction
data class ShareUrlInteraction(val url: String) : BottomSheetInteraction

data class SwitchProfileInteraction(val url: String, val crossProfile: CrossProfile) : BottomSheetInteraction

sealed interface AppInteraction : BottomSheetInteraction {
    val info: ActivityAppInfo?
    val modifier: ClickModifier
}

class AppClickInteraction(
    override val info: ActivityAppInfo?,
    override val modifier: ClickModifier,
    val index: Int,
    val type: ClickType,
) : AppInteraction

class ChoiceButtonInteraction(
    override val info: ActivityAppInfo?,
    override val modifier: ClickModifier,
) : AppInteraction

class PreferredAppChoiceButtonInteraction(
    override val info: ActivityAppInfo?,
    override val modifier: ClickModifier,
    val intent: Intent,
) : AppInteraction
