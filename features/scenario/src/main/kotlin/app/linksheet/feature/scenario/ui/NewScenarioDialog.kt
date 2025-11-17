package app.linksheet.feature.scenario.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.Webhook
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
import fe.composekit.component.dialog.SaneAlertDialogTextButton
import fe.composekit.component.dialog.SaneIconAlertDialog
import app.linksheet.feature.scenario.R
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun rememberNewScenarioDialog(onConfirm: (String) -> Unit): ResultDialogState<String> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberResultDialogState<String>()
    ResultDialog(state = state, onClose = onConfirm) {
        NewScenarioDialog(
            onDismiss = interaction.wrap(FeedbackType.Decline, state::dismiss),
            onConfirm = { name ->
                interaction.perform(FeedbackType.Confirm)
                state.close(name)
            }
        )
    }

    return state
}

@Composable
private fun NewScenarioDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val scenarioNameState = rememberTextFieldState()
    val state = rememberLazyListState()
    SaneIconAlertDialog(
        state = state,
        settings = ScrollbarSettings(enabled = false),
        innerModifier = Modifier
            .wrapContentHeight()
            .padding(horizontal = 16.dp),
        icon = {
            Icon(
                imageVector = Icons.Rounded.Functions,
                contentDescription = null
            )
        },
        title = content {
            Text(
                text = stringResource(id = R.string.settings_scenario__title_new_scenario_dialog),
                style = DialogTitleStyle,
            )
        },
        onDismiss = {},
        confirmButton = {
            Button(onClick = { onConfirm(scenarioNameState.text.toString()) }) {
                textContent(R.string.generic__button_text_create).content()
            }
        },
        dismissButton = {
            SaneAlertDialogTextButton(
                content = textContent(R.string.generic__button_text_cancel),
                onClick = onDismiss
            )
        }
    ) {
        TextField(
            state = scenarioNameState,
            placeholder = { Text(text = stringResource(R.string.settings_scenario__text_placeholder_name)) }
        )

//        TextContentWrapper(
//            textContent = annotatedStringResource(R.string.settings_remote_config__text_content)
//        )
    }
}

@Preview
@Composable
private fun NewScenarioDialogPreview() {
    NewScenarioDialog(
        onDismiss = {},
        onConfirm = {},
    )
}
