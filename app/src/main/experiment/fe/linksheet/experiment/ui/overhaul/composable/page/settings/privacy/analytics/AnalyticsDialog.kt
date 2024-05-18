package fe.linksheet.experiment.ui.overhaul.composable.page.settings.privacy.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import fe.android.compose.dialog.helper.result.ResultDialog
import fe.android.compose.dialog.helper.result.ResultDialogState
import fe.linksheet.R
import fe.linksheet.composable.util.LinkableTextView
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.dialog.DialogDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.module.analytics.TelemetryLevel
import fe.linksheet.ui.HkGroteskFontFamily


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
                item(key = R.string.telemetry_dialog_text, contentType = ContentTypeDefaults.Divider) {
                    LinkableTextView(
                        modifier = Modifier.padding(bottom = DialogDefaults.ContentPadding),
                        id = R.string.telemetry_dialog_text
                    )
                }

                items(items = telemetryLevels, key = { it.titleId }) { item ->
                    AnalyticsRadioButtonRow(
                        text = stringResource(id = item.titleId),
                        description = stringResource(id = item.descriptionId),
                        selected = selectedLevel == item,
                        onClick = { selectedLevel = item }
                    )
                }

                item(key = R.string.telemetry_dialog_text_2, contentType = ContentTypeDefaults.Divider) {
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

@Composable
fun AnalyticsRadioButtonRow(
    text: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(ShapeListItemDefaults.SingleShape)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
