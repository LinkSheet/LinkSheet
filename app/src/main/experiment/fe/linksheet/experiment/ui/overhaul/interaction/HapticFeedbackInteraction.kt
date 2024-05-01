package fe.linksheet.experiment.ui.overhaul.interaction

import android.content.ClipData
import android.content.ClipboardManager
import android.view.View
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.AndroidUriHandler
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.content.getSystemService
import fe.linksheet.experiment.ui.overhaul.composable.util.PLAIN_TEXT_LABEL


val LocalHapticFeedbackInteraction = staticCompositionLocalOf<HapticFeedbackInteraction> {
    error("LocalHapticInteraction")
}

interface HapticFeedbackInteraction {
    fun copy(content: String, type: HapticFeedbackType)

    fun openUri(uri: String, type: HapticFeedbackType)
}

class DefaultHapticFeedbackInteraction(private val view: View) : HapticFeedbackInteraction {
    private val clipboardManager = view.context.getSystemService<ClipboardManager>()!!
    private val uriHandler = AndroidUriHandler(view.context)

    override fun copy(content: String, type: HapticFeedbackType) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText(PLAIN_TEXT_LABEL, content))
        performHapticFeedback(type)
    }

    override fun openUri(uri: String, type: HapticFeedbackType) {
        uriHandler.openUri(uri)
        performHapticFeedback(type)
    }

    private fun performHapticFeedback(type: HapticFeedbackType) {
        view.performHapticFeedback(
            when (type) {
                HapticFeedbackType.LongPress -> 0
                HapticFeedbackType.TextHandleMove -> 9
                else -> -1
            }
        )
    }
}





