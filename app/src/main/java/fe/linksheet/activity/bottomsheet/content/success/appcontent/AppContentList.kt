package fe.linksheet.activity.bottomsheet.content.success.appcontent

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.util.toActivityAppInfo
import fe.composekit.component.shape.CustomShapeDefaults
import fe.kotlin.extension.iterable.getOrFirstOrNull
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.AppClickInteraction
import fe.linksheet.activity.bottomsheet.ClickModifier
import fe.linksheet.activity.bottomsheet.ClickType
import fe.linksheet.activity.bottomsheet.Interaction
import fe.linksheet.composable.util.debugBorder
import fe.linksheet.module.app.ActivityAppInfo
import fe.linksheet.module.debug.LocalUiDebug
import fe.linksheet.module.resolver.KnownBrowser

@Composable
fun AppContentList(
    apps: List<ActivityAppInfo>,
    uri: Uri?,
    appListSelectedIdx: Int,
    hasPreferredApp: Boolean,
    hideChoiceButtons: Boolean,
    showNativeLabel: Boolean,
    showPackage: Boolean,
    dispatch: (Interaction) -> Unit,
    isPrivateBrowser: (hasUri: Boolean, info: ActivityAppInfo) -> KnownBrowser?,
    showToast: (textId: Int, duration: Int, uiThread: Boolean) -> Unit,
) {
    val debug by LocalUiDebug.current.drawBorders.collectAsStateWithLifecycle()
    val state = rememberLazyListState()

    AppContent(
        info = apps.getOrFirstOrNull(appListSelectedIdx),
        appListSelectedIdx = appListSelectedIdx,
        hasPreferredApp = hasPreferredApp,
        hideChoiceButtons = hideChoiceButtons,
        dispatch = dispatch,
        showToast = showToast,
    ) { modifier ->
        LazyColumn(
            state = state,
            modifier = modifier.debugBorder(debug, 1.dp, Color.Green)
        ) {
            itemsIndexed(
                items = apps,
                key = { _, item -> item.flatComponentName }
            ) { index, info ->
                AppListItem(
                    appInfo = info,
                    selected = if (!hasPreferredApp) index == appListSelectedIdx else null,
                    onClick = { type, modifier ->
                        dispatch(AppClickInteraction(index, info, type, modifier))
                    },
                    preferred = false,
                    privateBrowser = isPrivateBrowser(uri != null, info),
                    showPackage = showPackage,
                    showNativeLabel = showNativeLabel,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AppListItem(
    appInfo: ActivityAppInfo,
    selected: Boolean?,
    onClick: (ClickType, ClickModifier) -> Unit,
    preferred: Boolean,
    privateBrowser: KnownBrowser?,
    showPackage: Boolean,
    showNativeLabel: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clip(CustomShapeDefaults.SingleShape)
            .background(if (selected == true) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .combinedClickable(
                onClick = { onClick(ClickType.Single, ClickModifier.None) },
                onDoubleClick = { onClick(ClickType.Double, ClickModifier.None) },
                onLongClick = { onClick(ClickType.Long, ClickModifier.None) }
            )
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = AppListItemRowDefaults.RowHeight)
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppListItemRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                appInfo = appInfo,
                preferred = preferred,
                showPackage = showPackage,
            )

            if (privateBrowser != null) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    // TODO: Checkout if we should reduce this button's size
                    FilledTonalIconButton(onClick = {
                        onClick(
                            ClickType.Single,
                            ClickModifier.Private(privateBrowser)
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = stringResource(id = R.string.request_private_browsing)
                        )
                    }
                }
            }
        }
    }
}

@Preview(group = "AppContentList", showBackground = true)
@Composable
private fun AppContentListPreview_Short() {
    val apps = remember {
        listOf(
            PackageInfoFakes.Youtube.toActivityAppInfo(),
            PackageInfoFakes.DuckDuckGoBrowser.toActivityAppInfo(),
            PackageInfoFakes.ChromeBrowser.toActivityAppInfo()
        )
    }

    AppContentListPreviewBase(apps = apps)
}

@Preview(group = "AppContentList", showBackground = true)
@Composable
private fun AppContentListPreview_Long() {
    val apps = remember {
        PackageInfoFakes.allResolved.map { it.toActivityAppInfo() }
    }

    AppContentListPreviewBase(apps = apps)
}

@Composable
private fun AppContentListPreviewBase(apps: List<ActivityAppInfo>) {
    AppContentList(
        apps = apps,
        uri = Uri.parse("https://linksheet.app"),
        appListSelectedIdx = -1,
        hasPreferredApp = false,
        hideChoiceButtons = false,
        showNativeLabel = false,
        showPackage = false,
        dispatch = { },
        isPrivateBrowser = { _, _ -> null },
        showToast = { _, _, _ -> }
    )
}
