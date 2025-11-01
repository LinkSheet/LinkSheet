package fe.linksheet.activity.bottomsheet.content.success

import android.net.Uri
import androidx.compose.runtime.Composable
import app.linksheet.feature.browser.Browser
import fe.linksheet.activity.bottomsheet.Interaction
import fe.linksheet.activity.bottomsheet.content.success.appcontent.AppContentGrid
import fe.linksheet.activity.bottomsheet.content.success.appcontent.AppContentList
import app.linksheet.feature.app.ActivityAppInfo

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
    dispatch: (Interaction) -> Unit,
    isPrivateBrowser: (hasUri: Boolean, info: ActivityAppInfo) -> Browser?,
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
            dispatch = dispatch,
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
            dispatch = dispatch,
        )
    }
}
