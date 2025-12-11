package fe.linksheet.composable.dialog

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import app.linksheet.feature.app.DomainVerificationAppInfo
import app.linksheet.feature.app.LinkHandling
import fe.android.compose.dialog.helper.input.InputResultDialog
import fe.android.compose.dialog.helper.input.InputResultDialogState
import fe.android.compose.dialog.helper.input.rememberInputResultDialog
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.wrap
import kotlinx.parcelize.Parcelize

@Composable
fun rememberDomainVerificationAppInfoDialog(
    onClose: (AppHostDialogResult) -> Unit,
): InputResultDialogState<DomainVerificationDialogData, AppHostDialogResult> {
    val interaction = LocalHapticFeedbackInteraction.current
    val state = rememberInputResultDialog<DomainVerificationDialogData, AppHostDialogResult>()

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
    val map = mutableStateMapOf<String, Boolean>()
    when (appInfo.linkHandling) {
        LinkHandling.Unsupported, LinkHandling.Browser -> for (host in preferredHosts) {
            map[host] = true
        }
        else -> for (host in appInfo.hostSet) {
            map[host] = host in preferredHosts
        }
    }

    return map
}
