package app.linksheet.feature.backup.impl.ui.exportimport

import android.content.Intent
import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import app.linksheet.compose.theme.DialogTitleStyle
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.api.RestoreMode
import app.linksheet.feature.backup.impl.R
import fe.android.compose.dialog.helper.result.ResultDialog
import fe.android.compose.dialog.helper.result.ResultDialogState
import fe.android.compose.dialog.helper.result.rememberResultDialogState
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.wrap
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContentWrapper
import fe.composekit.component.ContentType
import fe.composekit.component.dialog.DialogDefaults
import fe.composekit.component.dialog.SaneAlertDialogTextButton
import fe.composekit.component.dialog.SaneIconAlertDialog2
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.RadioButtonListItem
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import app.linksheet.compose.R as CommonR


@Parcelize
data class ImportResult(
    val result: @RawValue FileSelectionResult,
    val settings: ImportSettings
) : Parcelable

@Composable
fun rememberImportSettingsDialog(
    importIntent: Intent,
    onResult: (ImportResult) -> Unit
): ResultDialogState<ImportResult> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberResultDialogState<ImportResult>()

    ResultDialog(state = state, onClose = onResult) {
        ImportSettingsDialog(
            importIntent = importIntent,
            onDismiss = interaction.wrap(FeedbackType.Decline, state::dismiss),
            onResult = interaction.wrap(FeedbackType.Confirm, state::close),
        )
    }

    return state
}

@Composable
private fun ImportSettingsDialog(
    importIntent: Intent,
    onDismiss: () -> Unit,
    onResult: (ImportResult) -> Unit
) {
    var settings by rememberSaveable { mutableStateOf(ImportSettings.Default) }
    val fileSelectedLauncher = rememberFileSelectedLauncher { result ->
        if (result is FileSelectionResult.Ok) {
            onResult(ImportResult(result, settings))
        }
    }

    val state = rememberLazyListState()
    SaneIconAlertDialog2(
        modifier = Modifier.semantics { testTagsAsResourceId = true },
        state = state,
        icon = {
            Icon(
                imageVector = Icons.Rounded.SettingsBackupRestore,
                contentDescription = stringResource(id = R.string.settings_backup__title_restore_dialog)
            )
        },
        title = content {
            Text(
                text = stringResource(id = R.string.settings_backup__title_restore_dialog),
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
                content = textContent(CommonR.string.generic__button_text_confirm),
                onClick = { fileSelectedLauncher.launch(importIntent) }
            )
        }
    ) {
        DialogContent(state = state, settings = settings, onChange = { settings = it })
    }
}


private val RestoreMode.titleStringRes: Int
    get() = when (this) {
        RestoreMode.EraseRestore -> R.string.settings_backup__restore_dialog_title_erase_restore_settings
        RestoreMode.Replace -> R.string.settings_backup__restore_dialog_title_replace_settings
        RestoreMode.Merge -> R.string.settings_backup__restore_dialog_title_merge_settings
    }

private val RestoreMode.subtitleStringRes: Int
    get() = when (this) {
        RestoreMode.EraseRestore -> R.string.settings_backup__restore_dialog_subtitle_erase_restore_settings
        RestoreMode.Replace -> R.string.settings_backup__restore_dialog_subtitle_replace_settings
        RestoreMode.Merge -> R.string.settings_backup__restore_dialog_subtitle_merge_settings
    }

@Composable
private fun BoxScope.DialogContent(
    state: LazyListState,
    settings: ImportSettings,
    onChange: (ImportSettings) -> Unit
) {
    LazyColumn(
        modifier = Modifier.selectableGroup(),
        state = state,
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        item(
            key = R.string.settings_backup__restore_dialog_text_information,
            contentType = ContentType.TextItem
        ) {
            TextContentWrapper(
                modifier = Modifier.padding(bottom = DialogDefaults.ContentPadding),
                textContent = annotatedStringResource(R.string.settings_backup__restore_dialog_text_information)
            )
        }

        items(
            items = RestoreMode.entries,
            key = { it.name },
            contentType = { ContentType.RadioItem }) { item ->
            RadioButtonListItem(
                selected = settings.mode == item,
                onSelect = { onChange(settings.copy(mode = item)) },
                position = ContentPosition.Leading,
                headlineContent = textContent(item.titleStringRes),
                supportingContent = textContent(item.subtitleStringRes),
                otherContent = null,
                width = DialogDefaults.RadioButtonWidth,
                innerPadding = DialogDefaults.ListItemInnerPadding,
                textOptions = DialogDefaults.ListItemTextOptions,
                colors = DialogDefaults.ListItemColors
            )
        }
    }
}
