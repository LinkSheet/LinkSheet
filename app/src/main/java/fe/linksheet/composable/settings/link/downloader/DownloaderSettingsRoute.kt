package fe.linksheet.composable.settings.link.downloader

import android.Manifest
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import fe.android.preference.helper.BasePreference
import fe.android.preference.helper.compose.RepositoryState
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.SettingEnabledCardColumn
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.module.viewmodel.DownloaderSettingsViewModel
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.AndroidVersion
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class)
@Composable
fun DownloaderSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: DownloaderSettingsViewModel = koinViewModel()
) {
    val writeExternalStoragePermissionState = downloaderPermissionState()

    SettingsScaffold(
        headline = stringResource(id = R.string.enable_downloader),
        onBackPressed = onBackPressed
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "enable_downloader") {
                SettingEnabledCardColumn(
                    checked = viewModel.enableDownloader.value,
                    onChange = {
                        requestDownloadPermission(
                            writeExternalStoragePermissionState,
                            viewModel,
                            viewModel.enableDownloader,
                            it
                        )
                    },
                    headline = stringResource(id = R.string.enable_downloader),
                    subtitle = stringResource(id = R.string.enable_downloader_explainer),
                    contentTitle = stringResource(id = R.string.options)
                )
            }

            item(key = "downloader_url_mime_type") {
                SwitchRow(
                    state = viewModel.downloaderCheckUrlMimeType,
                    viewModel = viewModel,
                    enabled = viewModel.enableDownloader.value,
                    headlineId = R.string.downloader_url_mime_type,
                    subtitleId = R.string.downloader_url_mime_type_explainer
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun downloaderPermissionState() = rememberPermissionState(
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

@OptIn(ExperimentalPermissionsApi::class)
fun requestDownloadPermission(
    permissionState: PermissionState,
    viewModel: BaseViewModel,
    state: RepositoryState<Boolean, Boolean, BasePreference.Preference<Boolean>>,
    newState: Boolean
) {
    if (!AndroidVersion.AT_LEAST_API_29_Q && !permissionState.status.isGranted) {
        permissionState.launchPermissionRequest()
    } else viewModel.updateState(state, newState)
}