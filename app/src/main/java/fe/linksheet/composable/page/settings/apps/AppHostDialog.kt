package fe.linksheet.composable.page.settings.apps

import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dns
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.android.compose.dialog.helper.input.InputResultDialog
import fe.android.compose.dialog.helper.input.InputResultDialogState
import fe.android.compose.dialog.helper.input.rememberInputResultDialog
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.wrap
import fe.android.compose.text.DefaultContent.Companion.text
import fe.composekit.component.dialog.DialogDefaults
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.CheckboxListItem
import fe.linksheet.R
import fe.linksheet.composable.ui.HkGroteskFontFamily
import fe.linksheet.module.app.AppInfo
import fe.linksheet.module.app.DomainVerificationAppInfo
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Composable
fun rememberAppHostDialog(
    onClose: (AppHostDialogResult) -> Unit,
): InputResultDialogState<AppHostDialogData, AppHostDialogResult> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberInputResultDialog<AppHostDialogData, AppHostDialogResult>()

    InputResultDialog(state = state, onClose = onClose) { data ->
        val (info, _) = data
        val selectedStates = remember(data) {
            data.states.toMutableStateMap()
        }

        AppHostDialog(
            hosts = info.hostSet.toTypedArray(),
            hostState = selectedStates,
            dismiss = interaction.wrap(FeedbackType.Decline, state::dismiss),
            close = {
                state.close(AppHostDialogResult(info, createState(data, selectedStates).toList()))
                interaction.perform(FeedbackType.Confirm)
            }
        )
    }

    return state
}

fun createState(data: AppHostDialogData, selectedStates: Map<String, Boolean>) = sequence<HostState> {
    for ((host, initialState) in data.states) {
        val currentState = selectedStates[host] ?: continue
        yield(HostState(host, initialState, currentState))
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
) : Parcelable {

    @IgnoredOnParcel
    val states by lazy {
        appInfo.hostSet.map { it to (it in preferredHosts) }
    }
}

@Parcelize
data class AppHostDialogResult(
    val info: AppInfo,
    val hosts: List<HostState>,
) : Parcelable

@Composable
private fun AppHostDialog(
    hosts: Array<String>,
    hostState: SnapshotStateMap<String, Boolean>,
    dismiss: () -> Unit,
    close: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Dns,
                contentDescription = stringResource(id = R.string.app_host_dialog__title_hosts)
            )
        },
        title = {
            Text(
                text = stringResource(id = R.string.app_host_dialog__title_hosts),
                fontFamily = HkGroteskFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        },
        text = {
            DialogContent(hosts = hosts, hostState = hostState)
        },
        onDismissRequest = dismiss,
        dismissButton = {
            TextButton(onClick = dismiss) {
                Text(text = stringResource(id = R.string.generic__button_text_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = close) {
                Text(text = stringResource(id = R.string.generic__button_text_save))
            }
        }
    )
}

@Composable
private fun DialogContent(hosts: Array<String>, hostState: SnapshotStateMap<String, Boolean>) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        HorizontalDivider()

        LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            items(items = hosts) { host ->
                CheckboxListItem(
                    host = host,
                    isChecked = hostState[host]!!,
                    onCheckedChange = {
                        hostState[host] = it
                    }
                )
            }
        }

        HorizontalDivider()
    }
}

@Composable
private fun LazyItemScope.CheckboxListItem(host: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
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

@Composable
@Preview
private fun AppHostDialogPreview() {
    val hostState = remember {
        mutableStateMapOf<String, Boolean>(
            "google.com" to false, "youtube.com" to true, "facebook.com" to false
        )
    }

    AppHostDialog(
        hosts = hostState.keys.toTypedArray(),
        hostState = hostState,
        dismiss = {

        },
        close = {

        },
    )
}
