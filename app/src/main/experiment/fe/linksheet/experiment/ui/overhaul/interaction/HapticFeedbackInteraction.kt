package fe.linksheet.experiment.ui.overhaul.interaction

import android.content.ClipData
import android.content.ClipboardManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.AndroidUriHandler
import androidx.core.content.getSystemService
import fe.linksheet.experiment.ui.overhaul.composable.util.PLAIN_TEXT_LABEL


val LocalHapticFeedbackInteraction = staticCompositionLocalOf<HapticFeedbackInteraction> {
    error("LocalHapticInteraction")
}

interface HapticFeedbackInteraction {
    @Deprecated(message = "Use new API")
    fun copy(content: String, type: HapticFeedbackType)

    @Deprecated(message = "Use new API")
    fun openUri(uri: String, type: HapticFeedbackType)

    fun copy(content: String, type: FeedbackType)
    fun openUri(uri: String, type: FeedbackType)

    fun perform(type: FeedbackType)
}

fun HapticFeedbackInteraction.wrap(fn: () -> Unit, type: FeedbackType): () -> Unit {
    return {
        fn()
        perform(type)
    }
}

@JvmInline
value class FeedbackType private constructor(val flag: Int) {
    companion object {
        val None = FeedbackType(-1)
        val LongPress = FeedbackType(HapticFeedbackConstants.LONG_PRESS)
        val TextHandleMove = FeedbackType(9)
        val Confirm = FeedbackType(16)
        val Decline = FeedbackType(17)
    }
}

val HapticFeedbackType.newType: FeedbackType
    get() = when (this) {
        HapticFeedbackType.LongPress -> FeedbackType.LongPress
        HapticFeedbackType.TextHandleMove -> FeedbackType.TextHandleMove
        else -> FeedbackType.None
    }


class DefaultHapticFeedbackInteraction(private val view: View) : HapticFeedbackInteraction {
    private val clipboardManager = view.context.getSystemService<ClipboardManager>()!!
    private val uriHandler = AndroidUriHandler(view.context)

    @Deprecated("Use new API")
    override fun copy(content: String, type: HapticFeedbackType) {
        copy(content, type.newType)
    }

    override fun copy(content: String, type: FeedbackType) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText(PLAIN_TEXT_LABEL, content))
        performHapticFeedback(type)
    }

    @Deprecated("Use new API")
    override fun openUri(uri: String, type: HapticFeedbackType) {
        openUri(uri, type.newType)
    }

    override fun openUri(uri: String, type: FeedbackType) {
        uriHandler.openUri(uri)
        performHapticFeedback(type)
    }

    override fun perform(type: FeedbackType) {
        performHapticFeedback(type)
    }

    private fun performHapticFeedback(type: FeedbackType) {
        view.performHapticFeedback(type.flag)
    }
}





