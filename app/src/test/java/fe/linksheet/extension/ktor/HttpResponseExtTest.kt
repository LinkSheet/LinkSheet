package fe.linksheet.extension.ktor

import assertk.assertThat
import assertk.assertions.isTrue
import fe.linksheet.testlib.core.BaseUnitTest
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest


internal class HttpResponseExtTest : BaseUnitTest {
    private suspend fun create(contentType: String): HttpResponse {
        val client = HttpClient(MockEngine {
            respond(content = "<html></html>", headers = headersOf(HttpHeaders.ContentType, contentType))
        })

        return client.get(urlString = "https://linksheet.app")
    }

    @org.junit.Test
    fun `test isHtml`() = runTest {
        assertThat(create("text/html").isHtml()).isTrue()
        assertThat(create("text/html; charset=utf-8").isHtml()).isTrue()
    }
}
