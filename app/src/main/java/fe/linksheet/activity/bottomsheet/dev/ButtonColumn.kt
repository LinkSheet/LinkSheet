package fe.linksheet.activity.bottomsheet.dev

import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.DevBottomSheet
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.ui.HkGroteskFontFamily

@Composable
fun ButtonColumn(
    bottomSheetViewModel: BottomSheetViewModel,
    enabled: Boolean,
    resources: Resources,
    onClick: (Boolean) -> Unit,
    showToast: (Int) -> Unit,
    ignoreLibRedirectClick: (LibRedirectResolver.LibRedirectResult.Redirected) -> Unit,
    hideDrawer: () -> Unit
) {
    val result = bottomSheetViewModel.resolveResult!!
    if (result !is BottomSheetResult.BottomSheetSuccessResult) return

    val utilButtonWidthSum = DevBottomSheet.utilButtonWidth * listOf(
        bottomSheetViewModel.enableCopyButton.value,
        bottomSheetViewModel.enableSendButton.value,
        bottomSheetViewModel.enableIgnoreLibRedirectButton.value,
        result.downloadable.isDownloadable(),
        bottomSheetViewModel.enableRequestPrivateBrowsingButton.value
    ).count { it }

    val configuration = LocalConfiguration.current
    val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val widthHalf = if (landscape) {
        DevBottomSheet.maxModalBottomSheetWidth
    } else LocalConfiguration.current.screenWidthDp.dp

    val useTwoRows = utilButtonWidthSum > widthHalf / 2
    val padding = PaddingValues(horizontal = 10.dp)
    Column(modifier = Modifier.fillMaxWidth()) {
        if (result.downloadable.isDownloadable()) {
            Spacer(modifier = Modifier.height(5.dp))
            if (!bottomSheetViewModel.useTextShareCopyButtons.value) {
                ElevatedButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp),
                    onClick = {
                        bottomSheetViewModel.startDownload(
                            resources,
                            result.uri,
                            result.downloadable as Downloader.DownloadCheckResult.Downloadable
                        )

                        if (!bottomSheetViewModel.downloadStartedToast.value) {
                            showToast(R.string.download_started)
                        }

                        hideDrawer()
                    }) {
                    Icon(
                        imageVector = Icons.Default.Download, contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(id = R.string.download),
                        fontFamily = HkGroteskFontFamily,
                        maxLines = 1,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                TextButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp),
                    onClick = {
                        bottomSheetViewModel.startDownload(
                            resources,
                            result.uri,
                            result.downloadable as Downloader.DownloadCheckResult.Downloadable
                        )

                        if (!bottomSheetViewModel.downloadStartedToast.value) {
                            showToast(R.string.download_started)
                        }

                        hideDrawer()
                    }) {
                    Icon(
                        imageVector = Icons.Default.Download, contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(id = R.string.download),
                        fontFamily = HkGroteskFontFamily,
                        maxLines = 1,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

        }
        if (useTwoRows) {
            OpenButtons(
                bottomSheetViewModel = bottomSheetViewModel,
                enabled = enabled,
                onClick = onClick
            )
        }
        val libRedirectResult = result.libRedirectResult
        if (bottomSheetViewModel.enableIgnoreLibRedirectButton.value && libRedirectResult is LibRedirectResolver.LibRedirectResult.Redirected) {
            ElevatedOrTextButton(
                textButton = bottomSheetViewModel.useTextShareCopyButtons.value, onClick = {
                    ignoreLibRedirectClick(libRedirectResult)
                }, buttonText = R.string.ignore_libredirect
            )
        }


        if (!useTwoRows && bottomSheetViewModel.appInfo.value != null) {
            OpenButtons(
                bottomSheetViewModel = bottomSheetViewModel,
                enabled = enabled,
                onClick = onClick
            )
        }
    }
}
