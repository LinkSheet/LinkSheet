package fe.linksheet.activity.bottomsheet

import android.net.Uri
import androidx.compose.runtime.Composable
import fe.linksheet.activity.bottomsheet.column.ClickModifier
import fe.linksheet.activity.bottomsheet.column.ClickType
import fe.linksheet.activity.bottomsheet.appcontent.AppContentGrid
import fe.linksheet.activity.bottomsheet.appcontent.AppContentList
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.resolver.DisplayActivityInfo

@Composable
fun AppContentRoot(
    gridLayout: Boolean,
    apps: List<DisplayActivityInfo>,
    uri: Uri?,
    appListSelectedIdx: Int,
    hasPreferredApp: Boolean,
    hideChoiceButtons: Boolean,
    showNativeLabel: Boolean,
    showPackage: Boolean,
    launch: (info: DisplayActivityInfo, modifier: ClickModifier) -> Unit,
    launch2: (
        index: Int,
        info: DisplayActivityInfo,
        type: ClickType,
        modifier: ClickModifier,
    ) -> Unit,
    isPrivateBrowser: (hasUri: Boolean, info: DisplayActivityInfo) -> KnownBrowser?,
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
