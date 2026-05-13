@file:OptIn(ExperimentalPermissionsApi::class)

package app.linksheet.feature.downloader.ui

import androidx.compose.runtime.getValue
import app.linksheet.feature.downloader.R
import app.linksheet.feature.downloader.navigation.DownloaderRoute
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.preference.helper.Preference
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.DividedSwitchListItem
import fe.composekit.layout.column.SaneLazyColumnGroupScope
import fe.composekit.preference.ViewModelStatePreference
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.composekit.route.Route

fun SaneLazyColumnGroupScope.downloaderListItem(
    statePreference: ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>,
    navigate: (Route) -> Unit,
) {
    item(key = R.string.enable_downloader) { padding, shape ->
        val writeExternalStoragePermissionState = downloaderPermissionState()
        val enableDownloader by statePreference.collectAsStateWithLifecycle()
        DividedSwitchListItem(
            shape = shape,
            padding = padding,
            checked = enableDownloader,
            onCheckedChange = {
                requestDownloadPermission(
                    writeExternalStoragePermissionState,
                    statePreference,
                    it
                )
            },
            onContentClick = { navigate(DownloaderRoute) },
            position = ContentPosition.Trailing,
            headlineContent = textContent(R.string.enable_downloader),
            supportingContent = annotatedStringResource(R.string.enable_downloader_explainer)
        )
    }
}
