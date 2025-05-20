package fe.linksheet.composable.page.settings.privacy.remoteconfig

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Webhook
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import fe.composekit.component.dialog.SaneAlertDialogTextButton
import fe.composekit.component.dialog.SaneIconAlertDialog
import fe.linksheet.R
import fe.linksheet.composable.ui.DialogTitleStyle
import my.nanihadesuka.compose.ScrollbarSettings


@Composable
fun rememberRemoteConfigDialog(onChanged: (Boolean) -> Unit): ResultDialogState<Boolean> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberResultDialogState<Boolean>()
    ResultDialog(state = state, onClose = onChanged) {
        RemoteConfigDialog(
            onDismiss = interaction.wrap(FeedbackType.Decline, state::dismiss),
            onConfirm = {
                interaction.perform(FeedbackType.Confirm)
                state.close(true)
            }
        )
    }

    return state
}


@Composable
private fun RemoteConfigDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val state = rememberLazyListState()
    SaneIconAlertDialog(
        state = state,
        settings = ScrollbarSettings(enabled = false),
        innerModifier = Modifier
            .wrapContentHeight()
            .padding(horizontal = 16.dp),
        icon = {
            Icon(
                imageVector = Icons.Rounded.Webhook,
                contentDescription = null
            )
        },
        title = content {
            Text(
                text = stringResource(id = R.string.settings_remote_config__title_dialog),
                style = DialogTitleStyle,
            )
        },
        onDismiss = {},
        confirmButton = {
            Button(onClick = onConfirm) {
                textContent(R.string.generic__button_text_enable).content()
            }
        },
        dismissButton = {
            SaneAlertDialogTextButton(
                content = textContent(R.string.generic__button_text_disable),
                onClick = onDismiss
            )
        }
    ) {
        TextContentWrapper(
            textContent = annotatedStringResource(R.string.settings_remote_config__text_content)
        )
    }
}

@Preview
@Composable
private fun RemoteConfigDialogPreview() {
    RemoteConfigDialog(
        onDismiss = {},
        onConfirm = {},
    )
}
