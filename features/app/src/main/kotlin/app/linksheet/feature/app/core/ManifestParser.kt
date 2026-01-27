package app.linksheet.feature.app.core

import android.content.Intent
import com.reandroid.apk.ApkModule
import com.reandroid.archive.ArchiveBytes
import com.reandroid.arsc.chunk.xml.ResXmlAttribute
import com.reandroid.arsc.chunk.xml.ResXmlElement
import java.io.FileInputStream

interface ManifestParser {
    fun parseHosts(bytes: ArchiveBytes): Set<String>
    fun parseHosts(path: String): Set<String>
}

class DefaultManifestParser : ManifestParser {
    override fun parseHosts(bytes: ArchiveBytes): Set<String> {
        val apkModule = ApkModule(bytes.createZipEntryMap())

        val manifestBlock = apkModule.getAndroidManifest()
        val activities = manifestBlock.getActivities(true)
        return activities.toSequence().flatMap { activity ->
            activity.getElements("intent-filter")
                .toSequence()
                .flatMap { getHosts(it) }
        }.toSet()
    }

    override fun parseHosts(path: String): Set<String> {
        val inputStream = FileInputStream(path)
        val archiveBytes = ArchiveBytes(inputStream)
        return parseHosts(archiveBytes)
    }

    private fun getHosts(intentFilter: ResXmlElement) = sequence {
        val actions = intentFilter.getElements("action")
        val categories = intentFilter.getElements("category")
        val actionNames = actions.toSequence().mapNotNull { it.getFirstAttribute("name")?.valueString }.toSet()
        val categoryNames = categories.toSequence().mapNotNull { it.getFirstAttribute("name")?.valueString }.toSet()
        val dataElements = intentFilter.getElements("data").toSequence().toList()
        val dataSchemes = dataElements
            .mapNotNull { it.getFirstAttribute("scheme")?.valueString }
            .toSet()

        if (Intent.ACTION_VIEW !in actionNames) return@sequence
        if (Intent.CATEGORY_DEFAULT !in categoryNames && Intent.CATEGORY_BROWSABLE !in categoryNames) return@sequence
        if ("http" !in dataSchemes && "https" !in dataSchemes) return@sequence

        for (data in dataElements) {
            val host = data.getFirstAttribute("host")?.valueString ?: continue
            yield(host)
        }
    }

    private fun Iterator<ResXmlElement>.toSequence() = sequence {
        for (it in this@toSequence) yield(it)
    }

    private fun ResXmlElement.getFirstAttribute(name: String): ResXmlAttribute? {
        return getFirstAttribute { it.name == name }
    }

    private fun ResXmlElement.getFirstAttribute(filter: (ResXmlAttribute) -> Boolean): ResXmlAttribute? {
        val attributes = getAttributes(filter)
        return when {
            attributes.hasNext() -> attributes.next()
            else -> null
        }
    }
}
