package fe.linksheet.composable.settings.link.downloader

import android.Manifest
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
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.module.viewmodel.DownloaderSettingsViewModel
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.AndroidVersion
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DownloaderSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: DownloaderSettingsViewModel = koinViewModel()
) {
    val writeExternalStoragePermissionState = downloaderPermissionState()

    SettingsScaffold(
        headline = stringResource(id = R.string.follow_redirects),
        onBackPressed = onBackPressed
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item(key = "enable_downloader") {
                SwitchRow(
                    checked = viewModel.enableDownloader.value,
                    onChange = {
                        requestDownloadPermission(
                            writeExternalStoragePermissionState,
                            viewModel,
                            viewModel.enableDownloader,
                            it
                        )
                    },
                    headlineId = R.string.enable_downloader,
                    subtitleId = R.string.enable_downloader_explainer
                )
            }

            item(key = "downloader_url_mime_type") {
                SwitchRow(
                    state = viewModel.downloaderCheckUrlMimeType,
                    viewModel = viewModel,
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