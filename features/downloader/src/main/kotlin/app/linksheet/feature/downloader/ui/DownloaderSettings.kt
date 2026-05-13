package app.linksheet.feature.downloader.ui

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.linksheet.compose.ConnectedToggleButtonFlowRow
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import app.linksheet.feature.downloader.R
import app.linksheet.feature.downloader.core.DownloaderMode
import app.linksheet.feature.downloader.viewmodel.DownloaderSettingsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.preference.helper.compose.StatePreference
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.toEnabledContentSet
import fe.composekit.component.list.item.type.SliderListItem
import fe.composekit.component.list.item.type.SwitchListItem
import fe.composekit.core.AndroidVersion
import fe.composekit.preference.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import app.linksheet.compose.R as CommonR

private val downloaderModes = listOf(DownloaderMode.Auto, DownloaderMode.Manual)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun DownloaderSettings(
    onBackPressed: () -> Unit,
    viewModel: DownloaderSettingsViewModel = koinViewModel(),
) {
    val writeExternalStoragePermissionState = downloaderPermissionState()
    val enableDownloader by viewModel.enableDownloader.collectAsStateWithLifecycle()
    val contentSet = remember(enableDownloader) { enableDownloader.toEnabledContentSet() }

    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings_links_downloader__title_downloader),
        onBackPressed = onBackPressed
    ) {
        item(key = R.string.enable_downloader, contentType = ContentType.SingleGroupItem) {
            SwitchListItem(
                checked = enableDownloader,
                onCheckedChange = { value ->
                    requestDownloadPermission(
                        writeExternalStoragePermissionState,
                        { viewModel.enableDownloader(it) },
                        value
                    )
                },
                position = ContentPosition.Trailing,
                headlineContent = textContent(R.string.enable_downloader),
                supportingContent = annotatedStringResource(R.string.enable_downloader_explainer),
            )
        }
        divider(id = CommonR.string.generic__text_mode)

        item(key = -CommonR.string.generic__text_mode, contentType = ContentType.SingleGroupItem) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val downloaderMode by viewModel.mode.collectAsStateWithLifecycle()
                ConnectedToggleButtonFlowRow(
                    current = downloaderMode,
                    items = downloaderModes,
                    onChange = { viewModel.mode(it) },
                    itemContent = {
                        Text(
                            text = stringResource(
                                id = when (it) {
                                    DownloaderMode.Auto -> R.string.settings_downloader__title_mode_auto
                                    DownloaderMode.Manual -> R.string.settings_downloader__title_mode_manual
                                }
                            )
                        )
                    }
                )

                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = stringResource(
                        id = when (downloaderMode) {
                            DownloaderMode.Auto -> R.string.settings_downloader__subtitle_mode_auto
                            DownloaderMode.Manual -> R.string.settings_downloader__subtitle_mode_manual
                        }
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        divider(id = CommonR.string.options)

        group(size = 2) {
            item(key = R.string.downloader_url_mime_type) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.downloaderCheckUrlMimeType,
                    headlineContent = textContent(R.string.downloader_url_mime_type),
                    supportingContent = textContent(R.string.downloader_url_mime_type_explainer),
                )
            }

            item(key = CommonR.string.request_timeout) { padding, shape ->
                val requestTimeout by viewModel.requestTimeout.collectAsStateWithLifecycle()

                SliderListItem(
                    enabled = contentSet,
                    shape = shape,
                    padding = padding,
                    valueRange = 0f..30f,
                    value = requestTimeout.toFloat(),
                    onValueChange = { viewModel.requestTimeout(it.toInt()) },
                    valueFormatter = { it.toInt().toString() },
                    headlineContent = textContent(CommonR.string.request_timeout),
                    supportingContent = annotatedStringResource(CommonR.string.request_timeout_explainer),
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
    state: StatePreference<Boolean>,
    newState: Boolean,
) {
    if (!AndroidVersion.isAtLeastApi29Q() && !permissionState.status.isGranted) {
        permissionState.launchPermissionRequest()
    } else state(newState)
}

@OptIn(ExperimentalPermissionsApi::class)
fun requestDownloadPermission(
    permissionState: PermissionState,
    state: (Boolean) -> Unit,
    newState: Boolean,
) {
    if (!AndroidVersion.isAtLeastApi29Q() && !permissionState.status.isGranted) {
        permissionState.launchPermissionRequest()
    } else state(newState)
}
