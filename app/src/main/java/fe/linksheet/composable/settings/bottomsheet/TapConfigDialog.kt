package fe.linksheet.composable.settings.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.TapConfig
import fe.linksheet.component.ContentTypeDefaults
import fe.linksheet.component.dialog.DialogDefaults
import fe.linksheet.component.list.base.ContentPosition
import fe.linksheet.component.list.item.type.RadioButtonListItem
import fe.linksheet.component.util.Resource.Companion.textContent
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
                item(key = ContentTypeDefaults.TextItem) {
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
