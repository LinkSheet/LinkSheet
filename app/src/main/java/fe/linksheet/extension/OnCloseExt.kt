package fe.linksheet.extension

import fe.android.compose.dialog.helper.OnClose
import fe.linksheet.experiment.ui.overhaul.interaction.FeedbackType
import fe.linksheet.experiment.ui.overhaul.interaction.HapticFeedbackInteraction

fun OnClose<Unit>.wrap(interaction: HapticFeedbackInteraction, type: FeedbackType): () -> Unit {
    return {
        invoke(Unit)
        interaction.perform(type)
    }
}
