package app.linksheet.composable.page.settings.privacy

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.linksheet.compose.InfoText
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import app.linksheet.module.viewmodel.privacy.PrivacyIncognitoSettingsViewModel
import fe.android.compose.dialog.helper.confirm.ConfirmActionDialog
import fe.android.compose.dialog.helper.confirm.rememberConfirmActionDialog
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.wrap
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.CommonDefaults
import fe.composekit.component.ContentType
import fe.composekit.component.list.column.shape.ShapeListItem
import fe.composekit.component.list.item.toEnabledContentSet
import fe.composekit.preference.BooleanVmPref
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.composekit.preference.fakeBooleanVM
import fe.composekit.route.Route
import fe.linksheet.R
import fe.linksheet.extension.android.showToast
import org.koin.androidx.compose.koinViewModel
import app.linksheet.compose.R as CommonR


@Composable
fun PrivacyIncognitoSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (Route) -> Unit,
    viewModel: PrivacyIncognitoSettingsViewModel = koinViewModel(),
) {
    val activity = LocalActivity.current
    PrivacyIncognitoSettingsRouteInternal(
        enabledPref = viewModel.enable,
        disableLogs = viewModel.disableLogs,
        disableLogPersistence = viewModel.disableLogPersistence,
        onDeleteClick = {
            viewModel.clearAll().invokeOnCompletion {
                activity?.showToast(
                    R.string.settings_privacy_incognito__toast_text_deleted,
                    Toast.LENGTH_SHORT
                )
            }
        },
        onBackPressed = onBackPressed
    )
}


@Composable
fun PrivacyIncognitoSettingsRouteInternal(
    enabledPref: BooleanVmPref,
    disableLogs: BooleanVmPref,
    disableLogPersistence: BooleanVmPref,
    onDeleteClick: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val interaction = LocalHapticFeedbackInteraction.current
    val confirmDeleteDialog = rememberConfirmActionDialog<Unit>()

    ConfirmActionDialog(
        state = confirmDeleteDialog,
        onConfirm = { onDeleteClick() },
        onDismiss = { _ -> }
    ) { _ ->
        PrivacyIncognitoDeleteDataDialog(
            dismiss = interaction.wrap(FeedbackType.Decline, confirmDeleteDialog::dismiss),
            confirm = interaction.wrap(FeedbackType.Confirm, confirmDeleteDialog::confirm),
        )
    }

    val enable by enabledPref.collectAsStateWithLifecycle()
    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings_privacy_incognito__title_incognito),
        onBackPressed = onBackPressed
    ) {
        item(
            key = R.string.settings_privacy_incognito__title_enable_incognito,
            contentType = ContentType.SingleGroupItem
        ) {
            PreferenceSwitchListItem(
                statePreference = enabledPref,
                headlineContent = textContent(R.string.settings_privacy_incognito__title_enable_incognito),
                supportingContent = textContent(R.string.settings_privacy_incognito__subtitle_incognito),
            )
        }

        divider(id = CommonR.string.options)

        group(size = 2) {
            item(key = R.string.settings_privacy_incognito__title_disable_logs) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = enable.toEnabledContentSet(),
                    padding = padding,
                    shape = shape,
                    statePreference = disableLogs,
                    headlineContent = textContent(R.string.settings_privacy_incognito__title_disable_logs),
                    supportingContent = textContent(R.string.settings_privacy_incognito__subtitle_disable_logs),
                )
            }
            item(key = R.string.settings_privacy_incognito__title_disable_log_persistence) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = enable.toEnabledContentSet(),
                    padding = padding,
                    shape = shape,
                    statePreference = disableLogPersistence,
                    headlineContent = textContent(R.string.settings_privacy_incognito__title_disable_log_persistence),
                    supportingContent = textContent(R.string.settings_privacy_incognito__subtitle_disable_log_persistence),
                )
            }
        }

        item(contentType = ContentType.Spacer) {
            Spacer(modifier = Modifier.height(20.dp))
        }

        item(contentType = ContentType.Divider) {
            HorizontalDivider()
        }

        item(contentType = ContentType.Spacer) {
            Spacer(modifier = Modifier.height(20.dp))
        }

        item(
            key = R.string.settings_privacy_incognito__title_stored_data,
            contentType = ContentType.SingleGroupItem
        ) {
            ShapeListItem(
                baseModifier = CommonDefaults.BaseModifier,
                headlineContent = textContent(R.string.settings_privacy_incognito__title_stored_data),
                supportingContent = textContent(R.string.settings_privacy_incognito__subtitle_stored_data),
                trailingContent = {
                    Box(
                        modifier = CommonDefaults.BaseContentModifier,
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(
                            onClick = { confirmDeleteDialog.open(Unit) },
                            contentPadding = ButtonDefaults.contentPaddingFor(
                                ButtonDefaults.MinHeight,
                                hasStartIcon = true
                            ),
                        ) {
                            Icon(
                                Icons.Rounded.DeleteOutline,
                                contentDescription = stringResource(id = CommonR.string.generic__button_text_delete),
                                modifier = Modifier.size(ButtonDefaults.iconSizeFor(ButtonDefaults.MinHeight)),
                            )
                            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(ButtonDefaults.MinHeight)))
                            Text(text = stringResource(id = CommonR.string.generic__button_text_delete))
                        }
                    }
                },
            )
        }

        item(contentType = ContentType.Spacer) {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item(
            key = R.string.settings_privacy_incognito__text_info,
            contentType = ContentType.TextItem
        ) {
            InfoText(textContent = annotatedStringResource(R.string.settings_privacy_incognito__text_info))
        }
    }
}

@Preview
@Composable
private fun PrivacyIncognitoSettingsRouteInternalPreview() {
    PrivacyIncognitoSettingsRouteInternal(
        enabledPref = fakeBooleanVM(true),
        disableLogs = fakeBooleanVM(true),
        disableLogPersistence = fakeBooleanVM(true),
        onDeleteClick = {},
        onBackPressed = {}
    )
}
