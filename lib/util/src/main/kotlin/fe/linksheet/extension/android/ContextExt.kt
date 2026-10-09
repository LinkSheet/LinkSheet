package fe.linksheet.extension.android

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor

fun Context.getCurrentLanguageTag(): String {
    return resources.configuration.getLocales()[0].toLanguageTag()
}

suspend fun <R> Context.useFileDescriptor(
    uri: Uri,
    mode: String,
    block: suspend (ParcelFileDescriptor) -> R
): R? {
    return contentResolver.useFileDescriptor(uri, mode, block)
}
