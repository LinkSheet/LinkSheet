package fe.linksheet.extension

import fe.android.compose.dialog.helper.OnClose
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.HapticFeedbackInteraction

fun OnClose<Unit>.wrap(interaction: HapticFeedbackInteraction, type: FeedbackType): () -> Unit {
    return {
        invoke(Unit)
        interaction.perform(type)
    }
}
