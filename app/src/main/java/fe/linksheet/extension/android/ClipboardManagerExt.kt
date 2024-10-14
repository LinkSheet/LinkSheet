package fe.linksheet.extension.android

import android.content.ClipData
import android.content.ClipboardManager

fun ClipboardManager.setText(label: String, text: String) = setPrimaryClip(ClipData.newPlainText(label, text))

fun ClipboardManager.getFirstText(): String? {
    return primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.text?.toString()
}
