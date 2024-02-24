package fe.linksheet.composable.settings.notification

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.module.viewmodel.NotificationSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun NotificationSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: NotificationSettingsViewModel = koinViewModel()
) {
    val context = LocalContext.currentActivity()

    SettingsScaffold(R.string.notifications, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item(key = "url_copied") {
                SwitchRow(
                    state = viewModel.urlCopiedToast,
                    headlineId = R.string.url_copied_toast,
                    subtitleId = R.string.url_copied_toast_explainer
                )
            }

            item(key = "download_started") {
                SwitchRow(
                    state = viewModel.downloadStartedToast,
                    headlineId = R.string.download_started_toast,
                    subtitleId = R.string.download_started_toast_explainer
                )
            }

            item(key = "opening_with_app") {
                SwitchRow(
                    state = viewModel.openingWithAppToast,
                    headlineId = R.string.opening_with_app_toast,
                    subtitleId = R.string.opening_with_app_toast_explainer
                )
            }


            item(key = "resolve_via") {
                SwitchRow(
                    state = viewModel.resolveViaToast,
                    headlineId = R.string.resolve_via_toast,
                    subtitleId = R.string.resolve_via_toast_explainer
                )
            }

            item(key = "resolve_via_failed") {
                SwitchRow(
                    state = viewModel.resolveViaFailedToast,
                    headlineId = R.string.resolve_via_failed_toast,
                    subtitleId = R.string.resolve_via_failed_toast_explainer
                )
            }
        }
    }
}
