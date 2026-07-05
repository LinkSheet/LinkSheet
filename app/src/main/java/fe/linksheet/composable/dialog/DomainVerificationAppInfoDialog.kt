package fe.linksheet.composable.dialog

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import app.linksheet.feature.app.core.DomainVerificationAppInfo
import fe.android.compose.dialog.helper.input.InputResultDialog
import fe.android.compose.dialog.helper.input.InputResultDialogState
import fe.android.compose.dialog.helper.input.rememberInputResultDialogState
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.wrap
import fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers.createHostState
import kotlinx.parcelize.Parcelize

@Composable
fun rememberDomainVerificationAppInfoDialog(
    onClose: (AppHostDialogResult) -> Unit,
): InputResultDialogState<DomainVerificationDialogData, AppHostDialogResult> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberInputResultDialogState<DomainVerificationDialogData, AppHostDialogResult>()

    InputResultDialog(state = state, onClose = onClose) { data ->
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
data class DomainVerificationDialogData(
    val appInfo: DomainVerificationAppInfo,
    val preferredHosts: Set<String>,
) : Parcelable

fun DomainVerificationDialogData.createState(): SnapshotStateMap<String, Boolean> {
    return createHostState(appInfo.hostSet, appInfo.linkHandling, preferredHosts)
}
