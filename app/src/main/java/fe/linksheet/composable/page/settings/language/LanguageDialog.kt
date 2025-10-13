package fe.linksheet.composable.page.settings.language

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fe.android.compose.dialog.helper.result.ResultDialog
import fe.android.compose.dialog.helper.result.ResultDialogState
import fe.android.compose.dialog.helper.result.rememberResultDialogState
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.wrap
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.dialog.DialogDefaults
import fe.composekit.component.dialog.SaneAlertDialog
import fe.composekit.component.dialog.SaneAlertDialogTextButton
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.RadioButtonListItem
import fe.linksheet.R
import app.linksheet.compose.theme.DialogTitleStyle
import fe.linksheet.module.language.DisplayLocaleItem
import fe.linksheet.module.language.LocaleItem
import fe.linksheet.module.viewmodel.LanguageSettingsViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.util.*

@Composable
fun rememberLanguageDialog(
    viewModel: LanguageSettingsViewModel = koinViewModel(),
): ResultDialogState<LocaleItem> {
    val deviceLocale by viewModel.deviceLocaleFlow.collectAsStateWithLifecycle(
        minActiveState = Lifecycle.State.RESUMED,
        initialValue = null
    )
    val currentLocale by viewModel.appLocaleItemFlow.collectAsStateWithLifecycle(
        minActiveState = Lifecycle.State.RESUMED,
        initialValue = null
    )
    val locales by viewModel.localesFlow.collectAsStateWithLifecycle(
        minActiveState = Lifecycle.State.RESUMED,
        initialValue = emptyList()
    )

    return rememberLanguageDialog(
        locales = locales,
        deviceLocale = deviceLocale,
        currentLocale = currentLocale,
        onChanged = { viewModel.update(it) }
    )
}

@Composable
private fun rememberLanguageDialog(
    locales: List<DisplayLocaleItem>,
    deviceLocale: Locale?,
    currentLocale: LocaleItem?,
    onChanged: (LocaleItem) -> Unit,
): ResultDialogState<LocaleItem> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberResultDialogState<LocaleItem>()

    ResultDialog(state = state, onClose = onChanged) {
        LanguageDialog(
            locales = locales,
            deviceLocale = deviceLocale,
            currentLocale = currentLocale,
            onDismiss = interaction.wrap(FeedbackType.Decline, state::dismiss),
            onConfirm = interaction.wrap(FeedbackType.Confirm, state::close)
        )
    }

    return state
}

@Composable
private fun LanguageDialog(
    locales: List<DisplayLocaleItem>,
    deviceLocale: Locale?,
    currentLocale: LocaleItem?,
    onDismiss: () -> Unit,
    onConfirm: (LocaleItem) -> Unit,
) {
    var selectedLocale by remember { mutableStateOf(currentLocale) }

    val state = rememberLazyListState()
    SaneAlertDialog(
        state = state,
        title = content {
            Text(
                text = stringResource(id = R.string.settings_language__dialog_title),
                style = DialogTitleStyle,
            )
        },
        onDismiss = {},
        confirmButton = {
            SaneAlertDialogTextButton(
                content = textContent(R.string.save),
                onClick = {
                    selectedLocale?.let { onConfirm(it) }
                }
            )
        },
        dismissButton = {
            SaneAlertDialogTextButton(
                content = textContent(R.string.cancel),
                onClick = onDismiss
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .selectableGroup()
                .matchParentSize(),
            state = state,
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(items = locales, key = { it.item.locale.hashCode() }) { displayItem ->
                LanguageListItem(
                    displayItem = displayItem,
                    isDeviceLanguage = displayItem.isDeviceLanguage,
                    selected = (selectedLocale == displayItem.item) || (selectedLocale?.locale == deviceLocale && displayItem.isDeviceLanguage),
                    onSelect = {
                        selectedLocale = displayItem.item
                    }
                )
            }
        }
    }
}

@Composable
private fun LanguageListItem(
    displayItem: DisplayLocaleItem,
    isDeviceLanguage: Boolean,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    RadioButtonListItem(
        selected = selected,
        onSelect = onSelect,
        position = ContentPosition.Leading,
        headlineContent = text(displayItem.item.displayName),
        supportingContent = when {
            isDeviceLanguage -> textContent(R.string.settings_language__text_system_language)
            else -> text(displayItem.currentLocaleName)
        },
        otherContent = null,
        width = DialogDefaults.RadioButtonWidth,
        innerPadding = DialogDefaults.ListItemInnerPadding,
        textOptions = DialogDefaults.ListItemTextOptions,
        colors = DialogDefaults.ListItemColors
    )
}


@Preview
@Composable
private fun LanguageDialogPreview() {
    val en = DisplayLocaleItem(LocaleItem(Locale.ENGLISH, "English"), "English", false)
    val de = DisplayLocaleItem(LocaleItem(Locale.GERMAN, "Deutsch"), "German", false)
    val fr = DisplayLocaleItem(LocaleItem(Locale.FRENCH, "Français"), "French", false)
    val it = DisplayLocaleItem(LocaleItem(Locale.ITALIAN, "Italiano"), "Italian", false)
    LanguageDialog(
        locales = listOf(en, de, fr, it),
        deviceLocale = null,
        currentLocale = en.item,
        onDismiss = {},
        onConfirm = {},
    )
}
