package fe.linksheet.experiment.ui.overhaul.composable.page.settings.debug.log

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import fe.android.compose.content.OptionalContent
import fe.android.compose.dialog.helper.confirm.ConfirmActionDialog
import fe.android.compose.dialog.helper.confirm.rememberConfirmActionDialog
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.wrap
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.OptionalTextContent
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContent
import fe.composekit.component.CommonDefaults
import fe.composekit.component.list.column.shape.ClickableShapeListItem
import fe.composekit.component.shape.CustomShapeDefaults
import fe.kotlin.extension.time.localizedString
import fe.linksheet.LogTextViewerRoute
import fe.linksheet.R
import fe.linksheet.composable.util.listState
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.logTextViewerSettingsRoute
import fe.linksheet.module.log.file.LogFileService
import fe.linksheet.module.viewmodel.LogSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewLogSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    viewModel: LogSettingsViewModel = koinViewModel(),
) {
    val interaction = LocalHapticFeedbackInteraction.current

    val files by viewModel.files.collectOnIO()
    val mapState = remember(files) {
        listState(files)
    }

    val startupTime = remember {
        viewModel.fileAppLogger.startupTime.localizedString()
    }

    val confirmDeleteDialog = rememberConfirmActionDialog<LogFileService.LogFile>()

    ConfirmActionDialog(
        state = confirmDeleteDialog,
        onConfirm = { file -> viewModel.deleteFileAsync(file) },
        onDismiss = { _ -> }
    ) { _ ->
        DeleteLogDialog(
            dismiss = interaction.wrap(FeedbackType.Decline, confirmDeleteDialog::dismiss),
            confirm = interaction.wrap(FeedbackType.Confirm, confirmDeleteDialog::confirm),
        )
    }

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.logs), onBackPressed = onBackPressed) {
        item(key = R.string.reset_app_link_verification_status) {
            LogSessionListItem(
                logRoute = LogTextViewerRoute(startupTime, null),
                navigate = navigate,
                headline = text(startupTime),
                subtitle = textContent(id = R.string.current_session)
            )
        }

        divider(id = R.string.log_files)

        listHelper(
            noItems = R.string.no_logs_found,
            listState = mapState,
            list = files,
            listKey = { it.millis },
        ) { file, padding, shape ->
            LogSessionListItem(
                logRoute = LogTextViewerRoute(file.localizedTime, file.file.name),
                navigate = navigate,
                shape = shape,
                padding = padding,
                headline = text(file.localizedTime),
                subtitle = text(file.file.name),
                trailingContent = {
                    FilledTonalIconButton(onClick = { confirmDeleteDialog.open(file) }) {
                        Icon(imageVector = Icons.Rounded.DeleteOutline, contentDescription = null)
                    }
                }
            )
        }
    }
}

@Composable
private fun LogSessionListItem(
    logRoute: LogTextViewerRoute,
    navigate: (String) -> Unit,
    shape: Shape = CustomShapeDefaults.SingleShape,
    padding: PaddingValues = CommonDefaults.EmptyPadding,
    headline: TextContent,
    subtitle: OptionalTextContent,
    trailingContent: OptionalContent = null,
) {
    val route = remember(logRoute) {
        logTextViewerSettingsRoute.buildNavigation(logRoute)
    }

    ClickableShapeListItem(
        shape = shape,
        padding = padding,
        role = Role.Button,
        onClick = { navigate(route) },
        headlineContent = headline,
        supportingContent = subtitle,
        trailingContent = trailingContent
    )
}
