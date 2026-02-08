package app.linksheet.feature.libredirect.ui

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DomainAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.compose.preview.PreviewContainer
import app.linksheet.compose.theme.DialogTitleStyle
import app.linksheet.feature.libredirect.R
import fe.android.compose.dialog.helper.result.ResultDialog
import fe.android.compose.dialog.helper.result.ResultDialogState
import fe.android.compose.dialog.helper.result.rememberResultDialogState
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.wrap
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContentWrapper
import fe.composekit.component.dialog.SaneAlertDialogTextButton
import fe.composekit.component.dialog.SaneIconAlertDialog
import fe.linksheet.TextValidator
import fe.linksheet.WebUriTextValidator
import kotlinx.coroutines.flow.map
import my.nanihadesuka.compose.ScrollbarSettings
import app.linksheet.compose.R as CommonR

@Composable
fun rememberLibRedirectInstanceDialog(onConfirm: (Uri) -> Unit): ResultDialogState<Uri> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberResultDialogState<Uri>()
    ResultDialog(state = state, onClose = onConfirm) {
        LibRedirectInstanceDialog(
            onDismiss = interaction.wrap(FeedbackType.Decline, state::dismiss),
            onConfirm = { uri ->
                interaction.perform(FeedbackType.Confirm)
                state.close(uri)
            }
        )
    }

    return state
}



@Composable
private fun LibRedirectInstanceDialog(
    onDismiss: () -> Unit,
    onConfirm: (Uri) -> Unit,
) {
    val validatingTextState = remember {
        ValidatingTextFieldState(validator = WebUriTextValidator)
    }

    val uri by validatingTextState.resultFlow.collectAsStateWithLifecycle(initialValue = null)
    val isError by validatingTextState.isError.collectAsStateWithLifecycle(initialValue = true)

    val state = rememberLazyListState()
    SaneIconAlertDialog(
        state = state,
        settings = ScrollbarSettings(enabled = false),
        innerModifier = Modifier
            .wrapContentHeight()
            .padding(horizontal = 16.dp),
        icon = {
            Icon(
                imageVector = Icons.Rounded.DomainAdd,
                contentDescription = null
            )
        },
        title = content {
            Text(
                text = stringResource(id = R.string.settings_libredirect__title_add_instance),
                style = DialogTitleStyle,
            )
        },
        onDismiss = {},
        confirmButton = {
            Button(
                enabled = !isError,
                onClick = {
                    uri?.let { onConfirm(it) }
                }
            ) {
                TextContentWrapper(textContent = textContent(CommonR.string.generic__button_text_create))
            }
        },
        dismissButton = {
            SaneAlertDialogTextButton(
                content = textContent(CommonR.string.generic__button_text_cancel),
                onClick = onDismiss
            )
        }
    ) {
        InstanceTextField(
            state = validatingTextState
        )
    }
}

class ValidatingTextFieldState<R>(
    private val validator: TextValidator<R>,
) {
    val textState = TextFieldState()
    val resultFlow = snapshotFlow { textState.text }.map {
        validator.validate(it.toString())
    }
    val isError = resultFlow.map { it == null }

    object Saver : androidx.compose.runtime.saveable.Saver<ValidatingTextFieldState<R>, Any> {

        override fun SaverScope.save(value: ValidatingTextFieldState<R>): Any? {
            error("")
//            return listOf(
//                validator.
//                with(TextFieldState.Saver) { save(value.textState) },
//            )
        }

        override fun restore(value: Any): ValidatingTextFieldState<R>? {
            error("")
//            val (text, selectionStart, selectionEnd, savedTextUndoManager) = value as List<*>
//            return TextFieldState(
//                initialText = text as String,
//                initialSelection =
//                    TextRange(start = selectionStart as Int, end = selectionEnd as Int),
//                initialTextUndoManager =
//                    with(TextUndoManager.Companion.Saver) { TextUndoManager.Companion.Saver.restore(savedTextUndoManager!!) }!!,
//            )
        }

    }
}



@Composable
private fun InstanceTextField(
    state: ValidatingTextFieldState<Uri>
) {
    val uri by state.resultFlow.collectAsStateWithLifecycle(initialValue = null)
    val isError = rememberSaveable(uri) { uri == null }

    TextField(
        state = state.textState,
        isError = isError,
        placeholder = { Text(text = stringResource(R.string.settings_libredirect__text_placeholder_instance)) },
        supportingText = {
//            Row {
                Text(
                    text = if (isError) "Not a valid uri!" else "",
                    modifier = Modifier.clearAndSetSemantics {}
                )
//                Spacer(Modifier.weight(1f))
//                Text("Limit: ${state.text.length}/$charLimit")
//            }
        },
//        onKeyboardAction = {
////            WebUriTextValidator.validate()
//        },
    )
}

@Preview(showBackground = true)
@Composable
private fun InstanceTextFieldPreview() {
    PreviewContainer {

    }
}

@Preview
@Composable
private fun LibRedirectInstanceDialogPreview() {
    PreviewContainer {
        LibRedirectInstanceDialog(
            onDismiss = {},
            onConfirm = {},
        )
    }
}
