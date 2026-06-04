package fe.linksheet.extension.android

import android.content.ContentResolver
import android.net.Uri
import android.os.ParcelFileDescriptor

suspend fun <R> ContentResolver.useFileDescriptor(uri: Uri, mode: String, block: suspend (ParcelFileDescriptor) -> R): R? {
    val fd = openFileDescriptor(uri, mode)
    return fd?.use { block(it) }
}
