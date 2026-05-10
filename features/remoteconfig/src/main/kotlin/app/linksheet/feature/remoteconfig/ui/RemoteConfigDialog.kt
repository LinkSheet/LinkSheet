package app.linksheet.feature.remoteconfig.ui

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.linksheet.compose.theme.DialogTitleStyle
import app.linksheet.feature.remoteconfig.R
import fe.android.compose.dialog.helper.result.ResultDialog
import fe.android.compose.dialog.helper.result.ResultDialogState
import fe.android.compose.dialog.helper.result.rememberResultDialogState
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContentWrapper
import fe.composekit.component.dialog.SaneAlertDialogTextButton
import fe.composekit.component.dialog.SaneIconAlertDialog
import my.nanihadesuka.compose.ScrollbarSettings
import app.linksheet.compose.R as CommonR

const val REMOTE_CONFIG_DIALOG__DISABLE_TEST_TAG = "remote_config_dialog__disable_test_tag"
const val REMOTE_CONFIG_DIALOG__ENABLE_TEST_TAG = "remote_config_dialog__enable_test_tag"

@Composable
fun rememberRemoteConfigDialog(onChanged: (Boolean) -> Unit): ResultDialogState<Boolean> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberResultDialogState<Boolean>()
    ResultDialog(state = state, onClose = onChanged) {
        RemoteConfigDialog(
            onSelect = {
                interaction.perform(type = if (it) FeedbackType.Confirm else FeedbackType.Decline)
                state.close(it)
            }
        )
    }

    return state
}


@Composable
private fun RemoteConfigDialog(
    onSelect: (Boolean) -> Unit,
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
            Button(
                modifier = Modifier.testTag(REMOTE_CONFIG_DIALOG__ENABLE_TEST_TAG),
                onClick = { onSelect(true) }
            ) {
                textContent(CommonR.string.generic__button_text_enable).content()
            }
        },
        dismissButton = {
            SaneAlertDialogTextButton(
                modifier = Modifier.testTag(REMOTE_CONFIG_DIALOG__DISABLE_TEST_TAG),
                content = textContent(CommonR.string.generic__button_text_disable),
                onClick = { onSelect(false) }
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
        onSelect = {},
    )
}
