package fe.buildsrc.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import org.w3c.dom.Document
import java.net.http.HttpResponse
import javax.xml.parsers.DocumentBuilderFactory

val gson: Gson = GsonBuilder().setPrettyPrinting().create()

inline fun <reified W : JsonElement> asJson(): HttpResponse.BodySubscriber<W> {
    val upstream = HttpResponse.BodySubscribers.ofInputStream()
    return HttpResponse.BodySubscribers.mapping(upstream) {
        it.bufferedReader().use { reader -> JsonParser.parseReader(reader) as W }
    }
}


private val dbf = DocumentBuilderFactory.newInstance()

val asXml: HttpResponse.BodySubscriber<Document>
    get() {
        val upstream = HttpResponse.BodySubscribers.ofInputStream()
        return HttpResponse.BodySubscribers.mapping(upstream) {
            it.buffered().use { stream -> dbf.newDocumentBuilder().parse(stream) }
        }
    }


val asString = HttpResponse.BodyHandlers.ofString()
