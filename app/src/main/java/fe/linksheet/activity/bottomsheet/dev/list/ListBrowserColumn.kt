package fe.linksheet.activity.bottomsheet.dev.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.DevBottomSheet
import fe.linksheet.composable.util.defaultRoundedCornerShape
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.ui.HkGroteskFontFamily
import fe.linksheet.util.PrivateBrowsingBrowser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListBrowserColumn(
    appInfo: DisplayActivityInfo,
    preferred: Boolean,
    privateBrowser: PrivateBrowsingBrowser?,
    showPackage: Boolean,
    launchApp: (DisplayActivityInfo, Boolean, Boolean) -> Unit,
    libRedirectResult: LibRedirectResolver.LibRedirectResult.Redirected? = null,
    ignoreLibRedirectClick: ((LibRedirectResolver.LibRedirectResult.Redirected) -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
            .clip(defaultRoundedCornerShape)
            .clickable { launchApp(appInfo, false, false) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp)
                .heightIn(min = DevBottomSheet.preferredAppItemHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    bitmap = appInfo.iconBitmap,
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

            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                if (privateBrowser != null) {
                    FilledTonalIconButton(onClick = { launchApp(appInfo, false, true) }) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = stringResource(id = R.string.request_private_browsing)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(5.dp))

                if(ignoreLibRedirectClick != null && libRedirectResult != null){
                    FilledTonalIconButton(onClick = { ignoreLibRedirectClick(libRedirectResult) }) {
                        Icon(
                            imageVector = Icons.Outlined.FastForward,
                            contentDescription = stringResource(id = R.string.request_private_browsing)
                        )
                    }
                }
            }
        }
    }
}
