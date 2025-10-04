package fe.linksheet.activity.bottomsheet.content.success.appcontent

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.linksheet.feature.browser.Browser
import fe.kotlin.extension.iterable.getOrFirstOrNull
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.AppClickInteraction
import fe.linksheet.activity.bottomsheet.ClickModifier
import fe.linksheet.activity.bottomsheet.ClickType
import fe.linksheet.activity.bottomsheet.Interaction
import fe.linksheet.composable.component.appinfo.AppInfoIcon
import fe.linksheet.composable.util.defaultRoundedCornerShape
import fe.linksheet.feature.app.ActivityAppInfo


data class GridItem(val info: ActivityAppInfo, val privateBrowsingBrowser: Browser? = null) {
    override fun toString(): String {
        return info.flatComponentName + (privateBrowsingBrowser?.hashCode() ?: -1)
    }
}

private fun createGridItems(
    apps: List<ActivityAppInfo>,
    uri: Uri?,
    isPrivateBrowser: (hasUri: Boolean, info: ActivityAppInfo) -> Browser?,
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
    apps: List<ActivityAppInfo>,
    uri: Uri?,
    appListSelectedIdx: Int,
    hasPreferredApp: Boolean,
    hideChoiceButtons: Boolean,
    showPackage: Boolean,
    dispatch: (Interaction) -> Unit,
    isPrivateBrowser: (hasUri: Boolean, info: ActivityAppInfo) -> Browser?,
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
        dispatch = dispatch,
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
                AppGridItem(
                    appInfo = info,
                    selected = if (!hasPreferredApp) index == appListSelectedIdx else null,
                    onClick = { type, modifier ->
                        dispatch(AppClickInteraction(info, modifier, index, type))
                    },
                    privateBrowser = privateBrowser,
                    showPackage = showPackage,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppGridItem(
    appInfo: ActivityAppInfo,
    selected: Boolean?,
    onClick: (ClickType, ClickModifier) -> Unit,
    privateBrowser: Browser?,
    showPackage: Boolean,
) {
    val context = LocalContext.current
    val clickModifier = privateBrowser?.let { ClickModifier.Private(it) } ?: ClickModifier.None

    Column(
        modifier = Modifier
            .fillMaxSize()
            .heightIn(min = 85.dp)
            .padding(start = 7.dp, top = 7.dp, end = 7.dp, bottom = 0.dp)
            .clip(defaultRoundedCornerShape)
            .background(if (selected == true) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .combinedClickable(
                onClick = { onClick(ClickType.Single, clickModifier) },
                onDoubleClick = { onClick(ClickType.Double, clickModifier) },
                onLongClick = { onClick(ClickType.Long, clickModifier) }
            )
            .padding(all = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(40.dp)) {
            AppInfoIcon(
                modifier = Modifier.align(Alignment.Center),
                appInfo = appInfo
            )

            if (privateBrowser != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .clip(IconButtonDefaults.filledShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(16.dp),
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = stringResource(id = R.string.request_private_browsing)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = appInfo.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (showPackage) {
            Text(
                modifier = Modifier.basicMarquee(velocity = 20.dp),
                text = appInfo.packageName,
                fontSize = 12.sp,
                lineHeight = 12.sp
            )
        }
    }
}
