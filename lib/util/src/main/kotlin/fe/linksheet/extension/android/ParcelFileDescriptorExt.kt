package fe.linksheet.extension.android

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import okio.BufferedSink
import okio.BufferedSource
import okio.buffer
import okio.sink
import okio.source
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileInputStream
import java.io.FileOutputStream

suspend fun Context.useDescriptor(uri: Uri, mode: String, block: suspend (ParcelFileDescriptor) -> Unit) {
    val fd = contentResolver.openFileDescriptor(uri, mode)
    fd?.use { block(it) }
}

fun ParcelFileDescriptor.bufferedWriter(): BufferedWriter {
    return FileOutputStream(fileDescriptor).bufferedWriter()
}

fun ParcelFileDescriptor.bufferedReader(): BufferedReader {
    return FileInputStream(fileDescriptor).bufferedReader()
}

fun ParcelFileDescriptor.bufferedSink(): BufferedSink {
    return FileOutputStream(fileDescriptor).sink().buffer()
}

fun ParcelFileDescriptor.bufferedSource(): BufferedSource {
    return FileInputStream(fileDescriptor).source().buffer()
}
