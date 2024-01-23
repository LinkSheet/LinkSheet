package fe.linksheet.activity.bottomsheet.dev.preferred

import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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
import fe.linksheet.activity.bottomsheet.dev.ButtonColumn
import fe.linksheet.composable.util.defaultRoundedCornerShape
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.ui.HkGroteskFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferredAppColumn(
    result: BottomSheetResult.BottomSheetSuccessResult,
    bottomSheetViewModel: BottomSheetViewModel,
    showPackage: Boolean,
    resources: Resources,
    showToast: (Int) -> Unit,
    ignoreLibRedirectClick: (LibRedirectResolver.LibRedirectResult.Redirected) -> Unit,
    hideDrawer: () -> Unit,
    launchApp: (BottomSheetResult.BottomSheetSuccessResult, DisplayActivityInfo, Boolean) -> Unit,
) {
    val filteredItem = result.filteredItem!!

    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
                .clip(defaultRoundedCornerShape)
                .clickable { launchApp(result, filteredItem, false) }
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
                        bitmap = filteredItem.iconBitmap,
                        contentDescription = filteredItem.label,
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = stringResource(
                                id = R.string.open_with_app,
                                filteredItem.label,
                            ),
                            fontFamily = HkGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (showPackage) {
                            Text(
                                text = filteredItem.packageName, fontSize = 12.sp
                            )
                        }
                    }
                }

                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    FilledTonalIconButton(onClick = {

                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = stringResource(id = R.string.request_private_browsing)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        ButtonColumn(
            bottomSheetViewModel = bottomSheetViewModel,
            enabled = true,
            resources = resources,
            onClick = { launchApp(result, filteredItem, it) },
            hideDrawer = hideDrawer,
            ignoreLibRedirectClick = ignoreLibRedirectClick,
            showToast = showToast
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 10.dp),
            color = MaterialTheme.colorScheme.outline.copy(0.25f)
        )

        Text(
            modifier = Modifier.padding(start = 15.dp),
            text = stringResource(id = R.string.use_a_different_app),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
