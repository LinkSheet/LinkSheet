package fe.linksheet.activity.bottomsheet.content.success.appcontent

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.linksheet.testing.PackageInfoFakes
import app.linksheet.testing.toActivityAppInfo
import fe.composekit.component.shape.CustomShapeDefaults
import fe.kotlin.extension.iterable.getOrFirstOrNull
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.ClickModifier
import fe.linksheet.activity.bottomsheet.ClickType
import fe.linksheet.activity.bottomsheet.ImprovedBottomSheet
import fe.linksheet.composable.component.appinfo.AppInfoIcon
import fe.linksheet.composable.ui.HkGroteskFontFamily
import fe.linksheet.module.app.ActivityAppInfo
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

                AppListItem(
                    appInfo = info,
                    selected = if (!hasPreferredApp) index == appListSelectedIdx else null,
                    onClick = { type, modifier ->
                        launch2(index, info, type, modifier)
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
    modifier: Modifier = Modifier,
    appInfo: ActivityAppInfo,
    selected: Boolean?,
    onClick: (ClickType, ClickModifier) -> Unit,
    preferred: Boolean,
    privateBrowser: KnownBrowser?,
    showPackage: Boolean,
    showNativeLabel: Boolean = false,
) {
    val context = LocalContext.current

//    val supporting = @Composable {
//        Text(
//            text = appInfo.packageName,
//        )
//    }
//
//    ShapeListItem(
//        modifier = Modifier
//            .combinedClickable(
////            role = Role.,
//                onClick = { onClick(ClickType.Single, ClickModifier.None) },
//                onDoubleClick = { onClick(ClickType.Double, ClickModifier.None) },
//                onLongClick = { onClick(ClickType.Long, ClickModifier.None) }
//            )
//            .selectable(selected = selected ?: false) {
//                onClick(ClickType.Single, ClickModifier.None)
//            },
//        headlineContent = { Text(text = appInfo.label) },
//        supportingContent = if (showPackage) supporting else null,
//        leadingContent = {
//            Image(
//                bitmap = appInfo.getIcon(context),
//                contentDescription = appInfo.label,
//                modifier = Modifier.size(32.dp)
//            )
//        }
//    )


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
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
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
                // TODO: Do we still need to use a constant here?
                .heightIn(min = ImprovedBottomSheet.preferredAppItemHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppInfoIcon(
                    appInfo = appInfo
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    if (preferred) {
                        Text(
                            text = stringResource(id = R.string.open_with_app, appInfo.label),
                            fontFamily = HkGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(text = appInfo.label)
                    }

                    if (showPackage) {
                        Text(
                            text = appInfo.packageName,
                            fontSize = 12.sp,
                            lineHeight = 12.sp
                        )
                    }
                }
            }

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
    val apps = listOf(
        PackageInfoFakes.Youtube.toActivityAppInfo( ),
        PackageInfoFakes.DuckDuckGoBrowser.toActivityAppInfo(),
        PackageInfoFakes.ChromeBrowser.toActivityAppInfo()
    )

    AppContentList(
        apps = apps,
        uri = Uri.parse("https://linksheet.app"),
        appListSelectedIdx = -1,
        hasPreferredApp = false,
        hideChoiceButtons = false,
        showNativeLabel = false,
        showPackage = false,
        launch = { _, _ -> },
        launch2 = { _, _, _, _ -> },
        isPrivateBrowser = { _, _ -> null },
        showToast = { _, _, _ -> }
    )
}

@Preview(group = "AppContentList", showBackground = true)
@Composable
private fun AppContentListPreview_Long() {
    val apps = PackageInfoFakes.allResolved.map {
        it.toActivityAppInfo()
    }

    AppContentList(
        apps = apps,
        uri = Uri.parse("https://linksheet.app"),
        appListSelectedIdx = -1,
        hasPreferredApp = false,
        hideChoiceButtons = false,
        showNativeLabel = false,
        showPackage = false,
        launch = { _, _ -> },
        launch2 = { _, _, _, _ -> },
        isPrivateBrowser = { _, _ -> null },
        showToast = { _, _, _ -> }
    )
}

