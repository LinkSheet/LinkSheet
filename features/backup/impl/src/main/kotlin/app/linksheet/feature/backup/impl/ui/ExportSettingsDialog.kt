package app.linksheet.feature.backup.impl.ui.exportimport

import android.content.Intent
import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.linksheet.compose.theme.DialogTitleStyle
import app.linksheet.feature.backup.impl.R
import fe.android.compose.dialog.helper.input.InputResultDialog
import fe.android.compose.dialog.helper.input.InputResultDialogState
import fe.android.compose.dialog.helper.input.rememberInputResultDialogState
import fe.android.compose.dialog.helper.result.ResultDialog
import fe.android.compose.dialog.helper.result.ResultDialogState
import fe.android.compose.dialog.helper.result.rememberResultDialogState
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.wrap
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContent
import fe.android.compose.text.TextContentWrapper
import fe.android.span.helper.composable.createAnnotatedString
import fe.composekit.component.ContentType
import fe.composekit.component.dialog.DialogDefaults
import fe.composekit.component.dialog.SaneAlertDialogTextButton
import fe.composekit.component.dialog.SaneIconAlertDialog
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.CheckboxListItem
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import app.linksheet.compose.R as CommonR

@Parcelize
data class ExportResult(
    val result: @RawValue FileSelectionResult,
    val settings: ExportSettings
) : Parcelable

@Parcelize
@Stable
data class ExportSettings(
    val includePreferences: Boolean,
    val includeExperiments: Boolean,
    val includeAppState: Boolean,
    val includeSelectionHistory: Boolean,
    val includeCache: Boolean,
) : Parcelable {
    fun isAnyEnabled(): Boolean {
        return includePreferences || includeExperiments || includeAppState || includeSelectionHistory || includeCache
    }
}

@Composable
fun rememberExportSettingsDialog2(
    onResult: (ExportResult) -> Unit
): InputResultDialogState<Intent, ExportResult> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberInputResultDialogState<Intent, ExportResult>()

    InputResultDialog(state = state, onClose = onResult) {
        ExportSettingsDialog(
            exportIntent = it,
            onDismiss = interaction.wrap(FeedbackType.Decline, state::dismiss),
            onResult = interaction.wrap(FeedbackType.Confirm, state::close),
        )
    }

    return state
}

@Composable
fun rememberExportSettingsDialog(
    exportIntent: Intent,
    onResult: (ExportResult) -> Unit
): ResultDialogState<ExportResult> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberResultDialogState<ExportResult>()

    ResultDialog(state = state, onClose = onResult) {
        ExportSettingsDialog(
            exportIntent = exportIntent,
            onDismiss = interaction.wrap(FeedbackType.Decline, state::dismiss),
            onResult = interaction.wrap(FeedbackType.Confirm, state::close),
        )
    }

    return state
}


@Composable
private fun ExportSettingsDialog(
    exportIntent: Intent,
    onDismiss: () -> Unit,
    onResult: (ExportResult) -> Unit
) {
    var settings by rememberSaveable {
        mutableStateOf(
            ExportSettings(
                includePreferences = true,
                includeExperiments = true,
                includeAppState = true,
                includeSelectionHistory = true,
                includeCache = true
            )
        )
    }

    val fileSelectedLauncher = rememberFileSelectedLauncher { result ->
        if (result is FileSelectionResult.Ok) {
            onResult(ExportResult(result, settings))
        }
    }

    val state = rememberLazyListState()
    SaneIconAlertDialog(
        modifier = Modifier.semantics { testTagsAsResourceId = true },
        state = state,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Backup,
                contentDescription = stringResource(id = R.string.settings_backup__title_backup_dialog)
            )
        },
        title = content {
            Text(
                text = stringResource(id = R.string.settings_backup__title_backup_dialog),
                style = DialogTitleStyle
            )
        },
        onDismiss = onDismiss,
        dismissButton = {
            SaneAlertDialogTextButton(
                content = textContent(CommonR.string.generic__button_text_cancel),
                onClick = onDismiss
            )
        },
        confirmButton = {
            SaneAlertDialogTextButton(
                enabled = settings.isAnyEnabled(),
                content = textContent(R.string.settings_backup__button_text_backup_to_file),
                onClick = { fileSelectedLauncher.launch(exportIntent) }
            )
        }
    ) {
        DialogContent(
            state = state,
            settings = settings,
            onChange = { settings = it }
        )
    }
}

@Composable
private fun BoxScope.DialogContent(
    state: LazyListState,
    settings: ExportSettings,
    onChange: (ExportSettings) -> Unit
) {
    LazyColumn(
        modifier = Modifier.matchParentSize(),
        state = state,
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        item(
            key = R.string.export_include_recommendation,
            contentType = ContentType.TextItem
        ) {
            TextContentWrapper(
                modifier = Modifier.padding(bottom = DialogDefaults.ContentPadding),
                textContent = annotatedStringResource(R.string.export_include_recommendation)
            )
        }

        item(
            key = R.string.settings_backup__title_preferences,
            contentType = ContentType.CheckboxItem
        ) {
            CheckboxListItem(
                headlineContent = textContent(R.string.settings_backup__title_preferences),
                isChecked = settings.includePreferences,
                onCheckedChange = { onChange(settings.copy(includePreferences = it)) },
            )
        }
        item(
            key = R.string.settings_backup__title_experiments,
            contentType = ContentType.CheckboxItem
        ) {
            CheckboxListItem(
                headlineContent = textContent(R.string.settings_backup__title_experiments),
                isChecked = settings.includeExperiments,
                onCheckedChange = { onChange(settings.copy(includeExperiments = it)) },
            )
        }
        item(
            key = R.string.settings_backup__title_app_state,
            contentType = ContentType.CheckboxItem
        ) {
            CheckboxListItem(
                headlineContent = textContent(R.string.settings_backup__title_app_state),
                isChecked = settings.includeAppState,
                onCheckedChange = { onChange(settings.copy(includeAppState = it)) },
//                    supportingContent = textContent(fe.linksheet.R.string.export_log_dialog__subtitle_redact_log),
            )
        }
        item(
            key = R.string.settings_backup__title_selection_history,
            contentType = ContentType.CheckboxItem
        ) {
            CheckboxListItem(
                headlineContent = textContent(R.string.settings_backup__title_selection_history),
                isChecked = settings.includeSelectionHistory,
                onCheckedChange = { onChange(settings.copy(includeSelectionHistory = it)) },
//                    supportingContent = textContent(fe.linksheet.R.string.export_log_dialog__subtitle_redact_log),
            )
        }
        item(
            key = R.string.settings_backup__title_cached_items,
            contentType = ContentType.CheckboxItem
        ) {
            CheckboxListItem(
                headlineContent = textContent(R.string.settings_backup__title_cached_items),
                isChecked = settings.includeCache,
                onCheckedChange = { onChange(settings.copy(includeCache = it)) },
//                    supportingContent = textContent(fe.linksheet.R.string.export_log_dialog__subtitle_redact_log),
            )
        }

//                item(key = R.string.include_log_hash_key, contentType = ContentType.TextItem) {
//                    CheckboxListItem(
//                        checked = includeLogHashKey,
//                        onCheckedChange = { includeLogHashKey = !includeLogHashKey },
//                        position = ContentPosition.Leading,
//                        headlineContent = textContent(R.string.include_log_hash_key),
//                        otherContent = null
//                    )
//                }
//
//                if (historyFlag) {
//                    item(key = R.string.include_history, contentType = ContentType.TextItem) {
//                        CheckboxListItem(
//                            checked = includeHistory,
//                            onCheckedChange = { includeHistory = !includeHistory },
//                            position = ContentPosition.Leading,
//                            headlineContent = textContent(R.string.include_log_hash_key),
//                            otherContent = null
//                        )
//                    }
//                }

        item(key = R.string.export_privacy, contentType = ContentType.TextItem) {
            createAnnotatedString(id = R.string.export_privacy)
        }
    }
}

@Composable
private fun LazyItemScope.CheckboxListItem(
    headlineContent: TextContent,
    supportingContent: TextContent? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    CheckboxListItem(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        position = ContentPosition.Leading,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
//        headlineContent = textContent(R.string.settings_backup__title_app_state),
//                    supportingContent = textContent(fe.linksheet.R.string.export_log_dialog__subtitle_redact_log),
        otherContent = null,
        innerPadding = DialogDefaults.ListItemInnerPadding.copy(
            vertical = 4.dp
        ),
//                    innerPadding = DialogDefaults.ListItemInnerPadding,
        textOptions = DialogDefaults.ListItemTextOptions,
        colors = DialogDefaults.ListItemColors
    )
}

@Preview
@Composable
private fun ExportSettingsDialogPreview() {
    ExportSettingsDialog(
        exportIntent = Intent(),
        onDismiss = {},
        onResult = {}
    )
}
