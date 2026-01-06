package fe.linksheet.web

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.ktor.http.*
import kotlin.test.Test

internal class ContentDispositionHelperTest {
    @Test
    fun `test encoded`() {
        val disposition = ContentDisposition(
            "attachment",
            listOf(HeaderValueParam(ContentDisposition.Parameters.FileNameAsterisk, "UTF-8''file%20name.jpg"))
        )
        assertThat(ContentDispositionHelper.getFileName(disposition)).isEqualTo("file name.jpg")
    }

    @Test
    fun `test prefer asterisk`() {
        val disposition = ContentDisposition(
            "attachment",
            listOf(
                HeaderValueParam(ContentDisposition.Parameters.FileName, "not this one.png"),
                HeaderValueParam(ContentDisposition.Parameters.FileNameAsterisk, "UTF-8''file%20name.jpg")
            )
        )
        assertThat(ContentDispositionHelper.getFileName(disposition)).isEqualTo("file name.jpg")
    }

    @Test
    fun `test no filename`() {
        val disposition = ContentDisposition(
            "attachment",
            listOf(HeaderValueParam(ContentDisposition.Parameters.FileNameAsterisk, "UTF-8''"))
        )
        assertThat(ContentDispositionHelper.getFileName(disposition)).isNull()
    }
}
