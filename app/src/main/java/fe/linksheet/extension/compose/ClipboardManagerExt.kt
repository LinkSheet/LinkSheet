package fe.linksheet.extension.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.Clipboard
import fe.composekit.extension.getFirstText

@Composable
fun Clipboard.ObserveClipboard(onChanged: (String?) -> Unit) {
    val callback by rememberUpdatedState(onChanged)

    DisposableEffect(nativeClipboard) {
        val listener = android.content.ClipboardManager.OnPrimaryClipChangedListener {
            callback(nativeClipboard.getFirstText())
        }

        nativeClipboard.addPrimaryClipChangedListener(listener)
        onDispose {
            nativeClipboard.removePrimaryClipChangedListener(listener)
        }
    }
}
