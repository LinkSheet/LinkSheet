package fe.linksheet.testlib.core

import android.os.Build
import java.io.IOException
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystems


// Via https://stackoverflow.com/a/46468788
@Throws(IOException::class)
fun initFileSystem(uri: URI?): FileSystem? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return null

    return runCatching { FileSystems.newFileSystem(uri, emptyMap<String?, Any?>()) }
        .recover { if (it is IllegalArgumentException) FileSystems.getDefault() else null }
        .getOrNull()
}
