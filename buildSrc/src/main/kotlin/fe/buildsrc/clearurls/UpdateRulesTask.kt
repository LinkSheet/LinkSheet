package fe.buildsrc.clearurls

import fe.buildsrc.util.asString
import fe.buildsrc.util.asXml
import fe.buildsrc.util.get
import fe.buildsrc.util.httpClient
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

abstract class UpdateRulesTask : DefaultTask() {
    @get:Input
    abstract val file: Property<String>
    @Input
    val rawUrl: String = "https://raw.githubusercontent.com/ClearURLs/Rules/master/data.min.json"

    @TaskAction
    fun fetch() {
        val jsonFile = project.file(file.get())

        val response = httpClient.send(get(rawUrl), asString)
        jsonFile.writeText(response.body())
    }
}
