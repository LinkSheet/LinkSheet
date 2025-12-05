package fe.linksheet.composable.dialog

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import app.linksheet.feature.app.AppInfo
import fe.android.compose.dialog.helper.input.InputResultDialog
import fe.android.compose.dialog.helper.input.InputResultDialogState
import fe.android.compose.dialog.helper.input.rememberInputResultDialog
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.wrap
import kotlinx.parcelize.Parcelize


@Composable
fun rememberAppInfoDialog(
    onClose: (AppHostDialogResult) -> Unit,
    onDismiss: (() -> Unit)? = null,
): InputResultDialogState<AppInfoDialogData, AppHostDialogResult> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberInputResultDialog<AppInfoDialogData, AppHostDialogResult>()

    InputResultDialog(state = state, onClose = onClose, onDismiss = onDismiss) { data ->
        val (info, _) = data
        val mutableStates = remember(data) { data.createState() }
        val states = remember(data) { mutableStates.toMap() }

        AppHostDialog(
            hosts = mutableStates.keys.toList(),
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

@Parcelize
data class AppInfoDialogData(
    val appInfo: AppInfo,
    val domains: Set<String>,
) : Parcelable

private fun AppInfoDialogData.createState(): SnapshotStateMap<String, Boolean> {
    val map = mutableStateMapOf<String, Boolean>()
    for (domain in domains) {
        map[domain] = false
    }

    return map
}
