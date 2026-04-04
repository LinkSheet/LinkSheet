package fe.linksheet.extension.android

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
