package fe.linksheet.composable.page.settings.interconnect

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
import androidx.compose.ui.unit.dp
import app.linksheet.compose.theme.DialogTitleStyle
import fe.android.compose.dialog.helper.result.ResultDialog
import fe.android.compose.dialog.helper.result.ResultDialogState
import fe.android.compose.dialog.helper.result.rememberResultDialogState
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.wrap
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.dialog.SaneAlertDialog
import fe.composekit.component.dialog.SaneAlertDialogTextButton
import fe.linksheet.R
import fe.linksheet.module.language.DisplayLocaleItem
import fe.linksheet.module.language.LocaleItem
import java.util.*

@Composable
private fun rememberInterconnectDialog(
    locales: List<DisplayLocaleItem>,
    deviceLocale: Locale?,
    currentLocale: LocaleItem?,
    onChanged: (LocaleItem) -> Unit,
): ResultDialogState<LocaleItem> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberResultDialogState<LocaleItem>()

    ResultDialog(state = state, onClose = onChanged) {
        InterconnectDialog(
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
private fun InterconnectDialog(
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
                text = stringResource(id = R.string.domain_selection_confirmation_title),
                style = DialogTitleStyle,
            )
        },
//        stringResource(
//            id = R.string.domain_selection_confirmation_subtitle,
//            appLabel
//        )
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

            }
        }
    }
}
