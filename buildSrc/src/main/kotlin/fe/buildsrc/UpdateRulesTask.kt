package fe.buildsrc

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

abstract class UpdateRulesTask : DefaultTask() {
    companion object {
        const val RULES_JSON_URL = "https://raw.githubusercontent.com/ClearURLs/Rules/master/data.min.json"
    }

    @get:Input
    abstract val file: Property<String>

    @TaskAction
    fun fetch() {
        val jsonFile = project.file(file.get())

        val request = HttpRequest.newBuilder()
            .uri(URI(RULES_JSON_URL))
            .GET()
            .build()

        val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())

        jsonFile.writeText(response.body())
    }
}
