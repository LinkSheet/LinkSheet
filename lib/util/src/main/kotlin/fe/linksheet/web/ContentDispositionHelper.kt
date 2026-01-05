package fe.linksheet.web

import fe.std.result.getOrNull
import fe.std.result.tryCatch
import io.ktor.http.*
import java.net.URLDecoder

object ContentDispositionHelper {
    private const val ASTERISK_ENCODING_PREFIX = "utf-8''"

    fun getFileName(header: ContentDisposition): String? {
        val fileNameAsterisk = header.parameter(ContentDisposition.Parameters.FileNameAsterisk)
            ?: return header.parameter(ContentDisposition.Parameters.FileName)

        if (!fileNameAsterisk.startsWith(ASTERISK_ENCODING_PREFIX, ignoreCase = true)) {
            return fileNameAsterisk
        }

        val encodedValue = fileNameAsterisk.substring(ASTERISK_ENCODING_PREFIX.length)
        if (encodedValue.isEmpty()) return null

        return tryCatch { URLDecoder.decode(encodedValue, Charsets.UTF_8) }.getOrNull()
    }
}
