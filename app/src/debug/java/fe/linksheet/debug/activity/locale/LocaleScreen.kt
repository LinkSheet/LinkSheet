package fe.linksheet.debug.activity.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fe.linksheet.composable.util.DashedBorderBox
import fe.linksheet.module.language.DisplayLocaleItem
import fe.linksheet.module.language.LocaleItem
import fe.linksheet.module.viewmodel.LanguageSettingsViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.*

@Composable
fun LocaleScreen(viewModel: LanguageSettingsViewModel = koinViewModel()) {
    val deviceLocale by viewModel.deviceLocaleFlow.collectAsStateWithLifecycle(initialValue = null)
    val appLocaleItem by viewModel.appLocaleItemFlow.collectAsStateWithLifecycle(initialValue = null)
    val locales by viewModel.localesFlow.collectAsStateWithLifecycle(initialValue = null)
    Column(
        modifier = Modifier.padding(all = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SelectionContainer {
            DashedBorderBox(
                text = AnnotatedString(text = "Locale default"),
                surface = MaterialTheme.colorScheme.surface,
                strokeWidth = 1.dp,
                color = Color.Gray,
                cornerRadius = 12.dp,
                padding = 8.dp,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val list = LocaleListCompat.getAdjustedDefault()
                for (i in 0 until list.size()) {
                    LanguageDebug(index = i, item = list.get(i))
                }
            }
        }

        DashedBorderBox(
            text = AnnotatedString(text = "LocaleListCompat.getDefault"),
            surface = MaterialTheme.colorScheme.surface,
            strokeWidth = 1.dp,
            color = Color.Gray,
            cornerRadius = 12.dp,
            padding = 8.dp
        ) {
            Text(text = "${AppCompatDelegate.getApplicationLocales()}")
        }

        DashedBorderBox(
            text = AnnotatedString(text = "Current device locale"),
            surface = MaterialTheme.colorScheme.surface,
            strokeWidth = 1.dp,
            color = Color.Gray,
            cornerRadius = 12.dp,
            padding = 8.dp
        ) {
            Text(text = "$deviceLocale")
        }

        Button(onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList()) }) {
            Text(text = "Reset")
        }

        Text(
            text = stringResource(id = fe.linksheet.R.string.greeting),
            fontSize = 30.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        locales?.let {
            LocaleDropdown(
                selected = appLocaleItem,
                options = it,
                onChange = { item ->
                    viewModel.update(item.item)
                }
            )
        }
    }
}


@Composable
private fun LanguageDebug(index: Int, item: Locale?) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "$index")
        Column {
            Text(text = "$item")
            Text(text = "${item?.toLanguageTag()}")
            Text(text = "${item?.isO3Language}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocaleDropdown(
    selected: LocaleItem?,
    options: List<DisplayLocaleItem>,
    onChange: (DisplayLocaleItem) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            value = selected?.displayName ?: "",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            for (option in options) {
                LocaleDropdownItem(
                    localeItem = option,
                    onClick = {
                        onChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LocaleDropdownItem(
    localeItem: DisplayLocaleItem,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(text = "${localeItem.item.displayName}, ${localeItem.item.locale}")
        },
        onClick = onClick
    )
}
