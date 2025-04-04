package fe.linksheet.activity.bottomsheet.content.success

import android.net.Uri
import androidx.compose.runtime.Composable
import fe.linksheet.activity.bottomsheet.ClickModifier
import fe.linksheet.activity.bottomsheet.ClickType
import fe.linksheet.activity.bottomsheet.content.success.appcontent.AppContentGrid
import fe.linksheet.activity.bottomsheet.content.success.appcontent.AppContentList
import fe.linksheet.module.app.ActivityAppInfo
import fe.linksheet.module.resolver.KnownBrowser

@Composable
fun AppContentRoot(
    gridLayout: Boolean,
    apps: List<ActivityAppInfo>,
    uri: Uri?,
    appListSelectedIdx: Int,
    hasPreferredApp: Boolean,
    hideChoiceButtons: Boolean,
    showNativeLabel: Boolean,
    showPackage: Boolean,
    launch: (info: ActivityAppInfo, modifier: ClickModifier) -> Unit,
    launch2: (
        index: Int,
        info: ActivityAppInfo,
        type: ClickType,
        modifier: ClickModifier,
    ) -> Unit,
    isPrivateBrowser: (hasUri: Boolean, info: ActivityAppInfo) -> KnownBrowser?,
    showToast: (textId: Int, duration: Int, uiThread: Boolean) -> Unit,
) {
    if (gridLayout) {
        AppContentGrid(
            apps = apps,
            uri = uri,
            appListSelectedIdx = appListSelectedIdx,
            hasPreferredApp = hasPreferredApp,
            hideChoiceButtons = hideChoiceButtons,
            showPackage = showPackage,
            isPrivateBrowser = isPrivateBrowser,
            showToast = showToast,
            launch = launch,
            launch2 = launch2
        )
    } else {
        AppContentList(
            apps = apps,
            uri = uri,
            appListSelectedIdx = appListSelectedIdx,
            hasPreferredApp = hasPreferredApp,
            hideChoiceButtons = hideChoiceButtons,
            showPackage = showPackage,
            showNativeLabel = showNativeLabel,
            isPrivateBrowser = isPrivateBrowser,
            showToast = showToast,
            launch = launch,
            launch2 = launch2
        )
    }
}
