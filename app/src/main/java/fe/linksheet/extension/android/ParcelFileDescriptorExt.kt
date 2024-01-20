package fe.linksheet.extension.android

import android.os.ParcelFileDescriptor
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
