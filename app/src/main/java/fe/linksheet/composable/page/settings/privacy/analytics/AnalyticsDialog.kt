package fe.linksheet.composable.page.settings.privacy.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import fe.android.compose.dialog.helper.result.ResultDialog
import fe.android.compose.dialog.helper.result.ResultDialogState
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.dialog.DialogDefaults
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.RadioButtonListItem
import fe.linksheet.R
import fe.linksheet.composable.util.LinkableTextView
import fe.linksheet.module.analytics.TelemetryLevel
import app.linksheet.compose.theme.HkGroteskFontFamily


@Composable
fun rememberAnalyticDialog(
    telemetryLevel: TelemetryLevel,
    onChanged: (TelemetryLevel) -> Unit,
): ResultDialogState<TelemetryLevel> {
    val state = remember { ResultDialogState<TelemetryLevel>() }
    ResultDialog(state = state, onClose = onChanged) {
        AnalyticsDialog(
            currentLevel = telemetryLevel,
            onConfirm = state::close
        )
    }

    return state
}


private val telemetryLevels = listOf(
    TelemetryLevel.Disabled,
    TelemetryLevel.Minimal,
    TelemetryLevel.Standard,
    TelemetryLevel.Exhaustive
)

@Composable
private fun AnalyticsDialog(currentLevel: TelemetryLevel, onConfirm: (TelemetryLevel) -> Unit) {
    var selectedLevel by remember { mutableStateOf(currentLevel) }
    AlertDialog(
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        icon = {
            Icon(
                imageVector = Icons.Outlined.Analytics,
                contentDescription = stringResource(id = R.string.telemetry_dialog_title)
            )
        },
        title = {
            Text(
                text = stringResource(id = R.string.telemetry_dialog_title),
                fontFamily = HkGroteskFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        },
        text = {
            LazyColumn(modifier = Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                item(key = R.string.telemetry_dialog_text, contentType = ContentType.TextItem) {
                    LinkableTextView(
                        modifier = Modifier.padding(bottom = DialogDefaults.ContentPadding),
                        id = R.string.telemetry_dialog_text
                    )
                }

                items(items = telemetryLevels, key = { it.titleId }) { item ->
                    RadioButtonListItem(
                        selected = selectedLevel == item,
                        onSelect = { selectedLevel = item },
                        position = ContentPosition.Leading,
                        headlineContent = textContent(item.titleId),
                        supportingContent = textContent(item.descriptionId),
                        otherContent = null,
                        width = DialogDefaults.RadioButtonWidth,
                        innerPadding = DialogDefaults.ListItemInnerPadding,
                        textOptions = DialogDefaults.ListItemTextOptions,
                        colors = DialogDefaults.ListItemColors
                    )
                }

                item(key = R.string.telemetry_dialog_text_2, contentType = ContentType.TextItem) {
                    LinkableTextView(
                        modifier = Modifier.padding(top = DialogDefaults.ContentPadding),
                        id = R.string.telemetry_dialog_text_2
                    )
                }
            }
        },
        onDismissRequest = {},
        dismissButton = null,
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedLevel) }) {
                Text(text = stringResource(id = R.string.save))
            }
        }
    )
}
