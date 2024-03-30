package fe.linksheet.extension.compose

import androidx.compose.runtime.*
import androidx.compose.ui.platform.ClipboardManager

@Composable
fun ClipboardManager.observeAsState(): State<String?> {
    val nativeManager = remember { nativeClipboard }
    val clipboardText = remember { mutableStateOf(getText()?.text) }

    DisposableEffect(nativeManager) {
        val listener = android.content.ClipboardManager.OnPrimaryClipChangedListener {
            clipboardText.value = getText()?.text
        }

        nativeManager.addPrimaryClipChangedListener(listener)
        onDispose {
            nativeManager.removePrimaryClipChangedListener(listener)
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
