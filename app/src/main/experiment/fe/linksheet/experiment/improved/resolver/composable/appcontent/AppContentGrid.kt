package fe.linksheet.experiment.improved.resolver.composable.appcontent

import android.net.Uri
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import fe.kotlin.extension.iterable.getOrFirstOrNull
import fe.linksheet.activity.bottomsheet.column.ClickModifier
import fe.linksheet.activity.bottomsheet.column.ClickType
import fe.linksheet.activity.bottomsheet.column.GridBrowserButton
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.resolver.DisplayActivityInfo


data class GridItem(val info: DisplayActivityInfo, val privateBrowsingBrowser: KnownBrowser? = null) {
    override fun toString(): String {
        return info.flatComponentName + (privateBrowsingBrowser?.hashCode() ?: -1)
    }
}

private fun createGridItems(
    apps: List<DisplayActivityInfo>,
    uri: Uri?,
    isPrivateBrowser: (hasUri: Boolean, info: DisplayActivityInfo) -> KnownBrowser?,
): List<GridItem> {
    val items = mutableListOf<GridItem>()

    for (info in apps) {
        items.add(GridItem(info))

        val privateBrowser = isPrivateBrowser(uri != null, info)
        if (privateBrowser != null) {
            items.add(GridItem(info, privateBrowser))
        }
    }

    return items
}

// TODO: Grid and List are pretty similar, refactor maybe?
@Composable
fun AppContentGrid(
    apps: List<DisplayActivityInfo>,
    uri: Uri?,
    appListSelectedIdx: Int,
    hasPreferredApp: Boolean,
    hideChoiceButtons: Boolean,
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
    val items = remember(apps, uri) {
        createGridItems(apps, uri, isPrivateBrowser)
    }

    AppContent(
        info = apps.getOrFirstOrNull(appListSelectedIdx),
        appListSelectedIdx = appListSelectedIdx,
        hasPreferredApp = hasPreferredApp,
        hideChoiceButtons = hideChoiceButtons,
//        showNativeLabel = showNativeLabel,
        launch = launch,
        showToast = showToast,
    ) { modifier ->
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Adaptive(85.dp)
        ) {
            itemsIndexed(
                items = items,
                key = { _, item -> item.toString() }
            ) { index, (info, privateBrowser) ->
                GridBrowserButton(
                    appInfo = info,
                    selected = if (!hasPreferredApp) index == appListSelectedIdx else null,
                    onClick = { type, modifier ->
                        launch2(index, info, type, modifier)
                    },
                    privateBrowser = privateBrowser,
                    showPackage = showPackage
                )
            }
        }
    }
}
