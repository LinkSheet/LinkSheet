package fe.buildsrc.mimetypes

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test

internal class UpdateMimeTypesTaskTest {
    //    @Test
    fun test() {
//        val mimeTypes = UpdateMimeTypeTaskImpl().fetch()
        val mimeTypes = listOf(
            MimeType("application/x-dvd-ifo", listOf("ifo", "bup")),
            MimeType("text/x-rsrc", listOf("r")),
            MimeType("text/x-d", emptyList())
        )
        val generator = MimeTypesGenerator.build("", mimeTypes)
        generator.writeTo(System.out)
    }
}
