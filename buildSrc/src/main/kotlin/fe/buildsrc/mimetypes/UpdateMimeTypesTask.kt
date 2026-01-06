package fe.buildsrc.mimetypes

import fe.buildsrc.util.asXml
import fe.buildsrc.util.get
import fe.buildsrc.util.httpClient
import fe.std.dom.extension.asIterable
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Element
import java.io.File

abstract class UpdateMimeTypesTask : DefaultTask() {
    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val baseDir: Property<File>

    @get:Input
    abstract val customMimeTypes: ListProperty<MimeType>

    @TaskAction
    fun fetch() {
        val mimeTypes = UpdateMimeTypeTaskImpl().fetch()
        val mergedTypes = MimeTypeMerger.merge(mimeTypes, customMimeTypes.get())

        val kotlinFile = MimeTypesGenerator.build(packageName.get(), mergedTypes)

//        val packageDir = kotlinFile.packageName.replace('.', File.separatorChar)
//        val dir = File(baseDir.get(), packageDir)
//        println(dir)
//        dir.mkdirs()

        kotlinFile.writeTo(baseDir.get())
    }
}


object MimeTypeMerger {
    fun merge(mimeTypes: List<MimeType>, customMimeType: List<MimeType>): List<MimeType> {
        val mimeTypeMap = mimeTypes.map { it.type to it.globPatterns.toMutableSet() }.toMap(LinkedHashMap())
        val customMimeTypeMap = customMimeType.map { it.type to it.globPatterns.toSet() }.toMap()

        for ((mimeType, patterns) in customMimeTypeMap) {
            if (mimeType in mimeTypeMap) {
                mimeTypeMap[mimeType]?.addAll(patterns)
            } else {
                mimeTypeMap[mimeType] = patterns.toMutableSet()
            }
        }

        return mimeTypeMap.map { (mimeType, patterns) -> MimeType(mimeType, patterns) }
    }
}

class UpdateMimeTypeTaskImpl {
    companion object {
        private const val MIME_TYPES_URL =
            "https://raw.githubusercontent.com/apache/tika/main/tika-core/src/main/resources/org/apache/tika/mime/tika-mimetypes.xml"
    }

    fun fetch(): List<MimeType> {
        val response = httpClient.send(get(MIME_TYPES_URL)) { asXml() }
        val document = response.body()
        val mimeTypes = document.getElementsByTagName("mime-type")
        return mimeTypes
            .asIterable()
            .filterIsInstance<Element>()
            .map { parseMimeTypeElement(it) }

//        val doc = db.parse(URL(url).openStream())
//        val request = HttpRequest.newBuilder()
//            .uri(URI(MIME_TYPES_URL))
//            .GET()
//            .build()
//
//        val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())

//        jsonFile.writeText(response.body())
    }

    private fun parseMimeTypeElement(element: Element): MimeType {
        val type = element.getAttribute("type")
        val globs = element.getElementsByTagName("glob")
            .asIterable()
            .filterIsInstance<Element>()
            .map {
                val pattern = it.getAttribute("pattern")
                when {
                    pattern.startsWith("*.") -> pattern.substring(2)
                    else -> pattern
                }
            }

        return MimeType(type = type, globPatterns = globs)
//        return MimeType(type = type, globPatterns = emptyList())
    }
}

data class MimeType(val type: String, val globPatterns: Collection<String>)
