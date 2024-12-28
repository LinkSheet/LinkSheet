package fe.linksheet.experiment.improved.resolver.composable.appcontent

import android.net.Uri
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import fe.kotlin.extension.iterable.getOrFirstOrNull
import fe.linksheet.activity.bottomsheet.column.ClickModifier
import fe.linksheet.activity.bottomsheet.column.ClickType
import fe.linksheet.activity.bottomsheet.column.ListBrowserColumn
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.resolver.DisplayActivityInfo

@Composable
fun AppContentList(
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
    AppContent(
        info = apps.getOrFirstOrNull(appListSelectedIdx),
        appListSelectedIdx = appListSelectedIdx,
        hasPreferredApp = hasPreferredApp,
        hideChoiceButtons = hideChoiceButtons,
//        showNativeLabel = showNativeLabel,
        launch = launch,
        showToast = showToast,
    ) { modifier ->
        LazyColumn(modifier = modifier) {
            itemsIndexed(
                items = apps,
                key = { _, item -> item.flatComponentName }
            ) { index, info ->
                val privateBrowser = isPrivateBrowser(uri != null, info)

                ListBrowserColumn(
                    appInfo = info,
                    selected = if (!hasPreferredApp) index == appListSelectedIdx else null,
                    onClick = { type, modifier ->
                        launch2(index, info, type, modifier)
                    },
                    preferred = false,
                    privateBrowser = privateBrowser,
                    showPackage = showPackage,
                    showNativeLabel = showNativeLabel
                )
            }
        }
    }
}
