package fe.linksheet.extension.compose

import androidx.compose.runtime.*
import androidx.compose.ui.platform.ClipboardManager

@Composable
fun ClipboardManager.observeAsState(): State<String?> {
    val clipboardText = remember { mutableStateOf(getText()?.text) }
    val listener = remember(nativeClipboard) {
        android.content.ClipboardManager.OnPrimaryClipChangedListener {
            clipboardText.value = getText()?.text
        }
    }

    DisposableEffect(nativeClipboard) {
        nativeClipboard.addPrimaryClipChangedListener(listener)
        onDispose {
            nativeClipboard.removePrimaryClipChangedListener(listener)
        }
    }

    return clipboardText
}


@Composable
fun ClipboardManager.ObserveClipboard(onChanged: (String?) -> Unit) {
    val callback by rememberUpdatedState(onChanged)
    val clipboardText by observeAsState()

    LaunchedEffect(clipboardText) {
        callback(clipboardText)
    }
}
