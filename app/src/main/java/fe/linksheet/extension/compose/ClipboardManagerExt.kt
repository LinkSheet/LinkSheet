package fe.linksheet.extension.compose

import androidx.compose.runtime.*
import androidx.compose.ui.platform.ClipboardManager

@Composable
fun ClipboardManager.ObserveClipboard(onChanged: (String?) -> Unit) {
    val callback by rememberUpdatedState(onChanged)

    DisposableEffect(nativeClipboard) {
        val listener = android.content.ClipboardManager.OnPrimaryClipChangedListener {
            callback(getText()?.text)
        }

        nativeClipboard.addPrimaryClipChangedListener(listener)
        onDispose {
            nativeClipboard.removePrimaryClipChangedListener(listener)
        }
    }
}
