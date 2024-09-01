package fe.linksheet.composable.settings.link.downloader

import android.Manifest
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import fe.android.preference.helper.compose.StatePreference
import fe.linksheet.util.AndroidVersion


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
    if (!AndroidVersion.AT_LEAST_API_29_Q && !permissionState.status.isGranted) {
        permissionState.launchPermissionRequest()
    } else state(newState)
}
