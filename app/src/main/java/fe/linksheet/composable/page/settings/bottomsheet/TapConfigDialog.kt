package fe.linksheet.composable.page.settings.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TouchApp
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
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.dialog.DialogDefaults
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.RadioButtonListItem
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.TapConfig
import fe.linksheet.ui.HkGroteskFontFamily


private val tapConfigOptions = listOf(TapConfig.None, TapConfig.SelectItem, TapConfig.OpenApp, TapConfig.OpenSettings)

@Composable
fun TapConfigDialog(
    type: TapType,
    currentConfig: TapConfig,
    onDismiss: () -> Unit,
    onConfirm: (TapConfig) -> Unit,
) {
    var selectedTapConfig by remember { mutableStateOf(currentConfig) }
    val title = stringResource(id = type.dialogTitle)

    AlertDialog(
        icon = { Icon(imageVector = Icons.Outlined.TouchApp, contentDescription = title) },
        title = {
            Text(
                text = stringResource(id = R.string.tap_customization_dialog_title, title),
                fontFamily = HkGroteskFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
//                style = MaterialTheme.typography.titleLarge.copy(fontSize = )
            )
        },
        text = {
            LazyColumn(modifier = Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                item(key = ContentType.TextItem) {
                    Text(
                        modifier = Modifier.padding(bottom = 6.dp),
                        text = stringResource(id = R.string.tap_customization_dialog_text)
                    )
                }

                items(items = tapConfigOptions, key = { it.name }) { item ->
                    RadioButtonListItem(
                        selected = selectedTapConfig == item,
                        onSelect = { selectedTapConfig = item },
                        position = ContentPosition.Leading,
                        headlineContent = textContent(item.id),
                        otherContent = null,
                        width = DialogDefaults.RadioButtonWidth,
                        innerPadding = DialogDefaults.ListItemInnerPadding,
                        textOptions = DialogDefaults.ListItemTextOptions,
                        colors = DialogDefaults.ListItemColors
                    )
                }
            }
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedTapConfig) }) {
                Text(text = stringResource(id = R.string.save))
            }
        }
    )
}
