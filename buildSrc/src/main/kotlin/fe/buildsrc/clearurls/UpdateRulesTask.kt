package fe.buildsrc.clearurls

import fe.clearurlskt.config.provider.ClearUrlBundleSerializer
import fe.clearurlskt.config.provider.ClearUrlV1Bundle
import fe.clearurlskt.config.provider.ProviderV1Serializer
import fe.clearurlskt.config.provider.toV2
import fe.buildsrc.util.asString
import fe.buildsrc.util.get
import fe.buildsrc.util.httpClient
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class UpdateRulesTask : DefaultTask() {
    @get:Input
    abstract val file: Property<String>

    @get:Input
    abstract val rawUrl: Property<String>

    init {
        rawUrl.convention("https://raw.githubusercontent.com/ClearURLs/Rules/master/data.min.json")
    }

    @TaskAction
    fun fetch() {
        val bundleFile = project.file(file.get())

        val rulesUrl = rawUrl.get()
        val response = httpClient.send(get(rulesUrl), asString)

        check(response.statusCode() in 200..299) {
            "Unable to download ClearURLs rules: HTTP ${response.statusCode()} from $rulesUrl"
        }

        val providers = response.body().byteInputStream().use { ProviderV1Serializer.handle(it) }
        check(!providers.isNullOrEmpty()) { "Downloaded ClearURLs rules contain no providers" }
        val bundle = ClearUrlV1Bundle(version = 1, providers = providers.map { it.toV2() })
        bundleFile.writeBytes(ClearUrlBundleSerializer.encodeBundle(bundle))
    }
}
