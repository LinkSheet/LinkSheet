package fe.linksheet.activity.bottomsheet.column

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.BottomSheetActivityImpl.Companion.preferredAppItemHeight
import fe.linksheet.activity.bottomsheet.LaunchApp
import fe.linksheet.composable.util.defaultRoundedCornerShape
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.ui.HkGroteskFontFamily

enum class ClickType {
    Single, Double, Long, Private, Once, Always
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ListBrowserColumn(
    modifier: Modifier = Modifier,
    appInfo: DisplayActivityInfo,
    selected: Boolean?,
    onClick: (ClickType) -> Unit,
    preferred: Boolean,
    privateBrowser: KnownBrowser?,
    showPackage: Boolean,
//    launchApp: LaunchApp,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
            .clip(defaultRoundedCornerShape)
            .background(if (selected == true) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .combinedClickable(
                onClick = { onClick(ClickType.Single) },
                onDoubleClick = { onClick(ClickType.Double) },
                onLongClick = { onClick(ClickType.Long) }
            )
//            .combinedClickable(onClick = onClick, onDoubleClick = {
//                launchApp(appInfo, false, false)
//            }

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp)
                // TODO: Do we still need to use a constant here?
                .heightIn(min = preferredAppItemHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    bitmap = appInfo.getIcon(context),
                    contentDescription = appInfo.label,
                    modifier = Modifier.size(32.dp)
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
                    FilledTonalIconButton(onClick = { onClick(ClickType.Private) }) {
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
