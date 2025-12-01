package fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers

import android.os.Parcelable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WebAssetOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import app.linksheet.compose.theme.DialogTitleStyle
import app.linksheet.feature.app.DomainVerificationAppInfo
import app.linksheet.feature.app.IAppInfo
import app.linksheet.feature.app.LinkHandling
import fe.android.compose.content.rememberOptionalContent
import fe.android.compose.dialog.helper.input.InputResultDialog
import fe.android.compose.dialog.helper.input.InputResultDialogState
import fe.android.compose.dialog.helper.input.rememberInputResultDialog
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.wrap
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContentWrapper
import fe.composekit.component.dialog.DialogDefaults
import fe.composekit.component.dialog.SaneAlertDialog
import fe.composekit.component.dialog.SaneAlertDialogTextButton
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.CheckboxListItem
import fe.linksheet.R
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Composable
fun rememberAppHostDialog(
    onClose: (AppHostDialogResult) -> Unit,
): InputResultDialogState<AppHostDialogData, AppHostDialogResult> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberInputResultDialog<AppHostDialogData, AppHostDialogResult>()

    InputResultDialog(state = state, onClose = onClose) { data ->
        val (info, _) = data
        val states = remember(data) { data.createState() }
        val mutableStates = remember(data) { states.toMutableStateMap() }

        AppHostDialog(
            hosts = states.map { it.first },
            hostState = mutableStates,
            onDismiss = interaction.wrap(FeedbackType.Decline, state::dismiss),
            close = {
                state.close(AppHostDialogResult(info, states.createResult(mutableStates)))
                interaction.perform(FeedbackType.Confirm)
            }
        )
    }

    return state
}

fun InitialState.createResult(selectedStates: Map<String, Boolean>): List<HostState> {
    return mapNotNull { (host, initialState) ->
        val currentState = selectedStates[host] ?: return@mapNotNull  null
        HostState(host, initialState, currentState)
    }
}


@Parcelize
data class HostState(
    val host: String,
    val previousState: Boolean,
    val currentState: Boolean,
) : Parcelable

@Parcelize
data class AppHostDialogData(
    val appInfo: DomainVerificationAppInfo,
    val preferredHosts: Set<String>,
) : Parcelable

typealias InitialState = List<Pair<String, Boolean>>

fun AppHostDialogData.createState(): InitialState {
    return when (appInfo.linkHandling) {
        LinkHandling.Unsupported, LinkHandling.Browser -> preferredHosts.map { it to true }
        else -> appInfo.hostSet.map { it to (it in preferredHosts) }
    }
}

@Parcelize
data class AppHostDialogResult(
    val info: @RawValue IAppInfo,
    val hosts: List<HostState>,
) : Parcelable

@Composable
private fun AppHostDialog(
    hosts: List<String>,
    hostState: SnapshotStateMap<String, Boolean>,
    onDismiss: () -> Unit,
    close: () -> Unit,
) {
    val hasHosts = hosts.isNotEmpty()

    val state = rememberLazyListState()
    SaneAlertDialog(
        state = state,
        title = content {
            Text(
                text = stringResource(R.string.app_host_dialog__title_hosts),
                style = DialogTitleStyle
            )
        },
        onDismiss = onDismiss,
        confirmButton = {
            SaneAlertDialogTextButton(
                content = textContent(
                    id = if (hasHosts) R.string.generic__button_text_save
                    else R.string.generic__button_text_close
                ),
                onClick = close
            )
        },
        dismissButton = rememberOptionalContent(hasHosts) {
            SaneAlertDialogTextButton(
                content = textContent(R.string.generic__button_text_cancel),
                onClick = onDismiss
            )
        }
    ) {
        DialogContent(state = state, hosts = hosts, hostState = hostState)
    }
}

@Composable
private fun BoxScope.DialogContent(
    state: LazyListState,
    hosts: List<String>,
    hostState: SnapshotStateMap<String, Boolean>,
) {
    if (hosts.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.WebAssetOff,
                contentDescription = null,
            )
            TextContentWrapper(
                modifier = Modifier.padding(bottom = DialogDefaults.ContentPadding),
                textContent = textContent(R.string.app_host_dialog__text_no_hosts)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.matchParentSize(),
            state = state,
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(items = hosts, key = { it }) { host ->
                CheckboxListItem(
                    host = host,
                    isChecked = hostState[host]!!,
                    onCheckedChange = {
                        hostState[host] = it
                    }
                )
            }
        }
    }
}

@Composable
fun LazyItemScope.CheckboxListItem(host: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val padding = DialogDefaults.ListItemInnerPadding.copy(
        vertical = 4.dp
    )

    CheckboxListItem(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        position = ContentPosition.Leading,
        headlineContent = text(host),
        otherContent = null,
        innerPadding = padding,
        textOptions = DialogDefaults.ListItemTextOptions,
        colors = DialogDefaults.ListItemColors
    )
}

data class HostStatePreview(
    val hosts: List<String>,
    val states: SnapshotStateMap<String, Boolean>,
)

private class HostStatePreviewProvider() : PreviewParameterProvider<HostStatePreview> {
    override val values: Sequence<HostStatePreview> = sequenceOf(
        HostStatePreview(
            listOf("google.com", "youtube.com", "facebook.com", "linksheet.app", "github.com", "discord.com"),
            mutableStateMapOf(
                "google.com" to false,
                "youtube.com" to true,
                "facebook.com" to false,
                "linksheet.app" to false,
                "github.com" to true,
                "discord.com" to false
            )
        ),
        HostStatePreview(
            listOf("google.com"),
            mutableStateMapOf(
                "google.com" to false,
            )
        ),
        HostStatePreview(
            listOf(),
            mutableStateMapOf(
            )
        ),
    )
}

@Composable
@Preview(showBackground = true)
private fun DialogContentPreview(
    @PreviewParameter(HostStatePreviewProvider::class) hostState: HostStatePreview,
) {
    Box {
        DialogContent(
            state = rememberLazyListState(),
            hosts = hostState.hosts,
            hostState = hostState.states,
        )
    }
}

@Composable
@Preview
private fun AppHostDialogPreview(
    @PreviewParameter(HostStatePreviewProvider::class) hostState: HostStatePreview,
) {
    AppHostDialog(
        hosts = hostState.hosts,
        hostState = hostState.states,
        onDismiss = {

        },
        close = {

        },
    )
}
