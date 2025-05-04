package fe.linksheet.extension.ktor

import assertk.assertThat
import assertk.assertions.isTrue
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class HttpResponseExtTest {
    private suspend fun create(contentType: String): HttpResponse {
        val client = HttpClient(MockEngine {
            respond(content = "<html></html>", headers = headersOf(HttpHeaders.ContentType, contentType))
        })

        return client.get(urlString = "https://linksheet.app")
    }

    @Test
    fun `test isHtml`() = runTest {
        assertThat(create("text/html").isHtml()).isTrue()
        assertThat(create("text/html; charset=utf-8").isHtml()).isTrue()
    }
}
