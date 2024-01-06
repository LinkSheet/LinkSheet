package fe.buildsrc

import org.gradle.api.logging.Logger
import java.io.File

object Locale {
    private val values = "values-"
    private val regex = Regex("^values-([a-z]{2})(-[a-b]+)?$")
    private val emptyFile = """
        <?xml version="1.0" encoding="utf-8"?>
        <resources></resources>
    """.trimIndent().encodeToByteArray()

    fun getLocales(logger: Logger, res: File): List<String> {
        return (res.listFiles() ?: emptyArray()).filter { !it.isDirectory }.mapNotNull { dir ->
            val groups = regex.matchEntire(dir.name)?.groupValues ?: return@mapNotNull null
            val lang = groups[1] + groups.getOrNull(2)

            val stringFile = File(dir, "strings.xml")
            if (!stringFile.exists()) {
                logger.error("$stringFile does not exist!")
                return@mapNotNull null
            }

            val fileBytes = stringFile.inputStream().use { it.readNBytes(emptyFile.size) }
            if (fileBytes == emptyFile) {
                logger.warn("$stringFile is empty!")
                return@mapNotNull null
            }

            lang
        }
    }
}
