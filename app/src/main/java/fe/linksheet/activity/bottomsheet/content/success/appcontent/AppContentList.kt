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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.compose.debug.LocalUiDebug
import app.linksheet.compose.debugBorder
import app.linksheet.compose.preview.PreviewDebugProvider
import app.linksheet.feature.app.core.ActivityAppInfo
import app.linksheet.feature.browser.core.Browser
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.fake.toActivityAppInfo
import fe.composekit.component.shape.CustomShapeDefaults
import fe.kotlin.extension.iterable.getOrFirstOrNull
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.AppClickInteraction
import fe.linksheet.activity.bottomsheet.ClickModifier
import fe.linksheet.activity.bottomsheet.ClickType
import fe.linksheet.activity.bottomsheet.AppInteraction

@Composable
fun AppContentList(
    apps: List<ActivityAppInfo>,
    uri: Uri?,
    appListSelectedIdx: Int,
    hasPreferredApp: Boolean,
    hideChoiceButtons: Boolean,
    showNativeLabel: Boolean,
    showPackage: Boolean,
    dispatch: (AppInteraction) -> Unit,
    isPrivateBrowser: suspend (hasUri: Boolean, info: ActivityAppInfo) -> Browser?,
) {
    val debug by LocalUiDebug.current.drawBorders.collectAsStateWithLifecycle()
    val state = rememberLazyListState()
    AppContent(
        info = apps.getOrFirstOrNull(appListSelectedIdx),
        appListSelectedIdx = appListSelectedIdx,
        hasPreferredApp = hasPreferredApp,
        hideChoiceButtons = hideChoiceButtons,
        dispatch = dispatch,
    ) { modifier ->
        LazyColumn(
            modifier = modifier.debugBorder(debug, 1.dp, Color.Green),
            state = state
        ) {
            itemsIndexed(
                items = apps,
                key = { _, item -> item.flatComponentName }
            ) { index, info ->
                var privateBrowser by remember { mutableStateOf<Browser?>(null) }
                LaunchedEffect(key1 = uri, key2 = info) {
                    privateBrowser = isPrivateBrowser(uri != null, info)
                }

                AppListItem(
                    appInfo = info,
                    selected = if (!hasPreferredApp) index == appListSelectedIdx else null,
                    onClick = { type, modifier ->
                        dispatch(AppClickInteraction(info, modifier, index, type))
                    },
                    preferred = false,
                    privateBrowser = privateBrowser,
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
    privateBrowser: Browser?,
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
    CompositionLocalProvider(LocalUiDebug provides PreviewDebugProvider()) {
        AppContentList(
            apps = apps,
            uri = "https://linksheet.app".toUri(),
            appListSelectedIdx = -1,
            hasPreferredApp = false,
            hideChoiceButtons = false,
            showNativeLabel = false,
            showPackage = false,
            dispatch = { },
            isPrivateBrowser = { _, _ -> null },
        )
    }
}
