package fe.linksheet.engine.resolver.redirects

import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.fail
import fe.linksheet.experiment.engine.resolver.redirects.resolveLocal
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.std.result.assert.assertFailure
import fe.std.result.assert.assertSuccess
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.koin.core.context.stopKoin

class FollowRedirectsLinkResolverSourceTest {
    companion object {
        private val input = Url("https://linksheet.app/redirect-me")
        private const val TARGET = "https://linksheet.app/target"
    }

    @Test
    fun `redirect on head request`(): Unit = runBlocking {
        val mockEngine = MockEngine { request ->
            when (request.url) {
                input -> respondRedirect(TARGET)
                else -> respondOk()
            }
        }

        val client = HttpClient(mockEngine)
        val result = resolveLocal(client, input)

        assertSuccess(result)
            .isInstanceOf<ResolveResultType.Resolved.Local>()
            .transform { it.url }
            .isEqualTo(TARGET)
    }

    @Test
    fun `return 400 on head, redirect on get`(): Unit = runBlocking {
        val mockEngine = MockEngine { request ->
            when (request.method) {
                HttpMethod.Head -> respondBadRequest()
                HttpMethod.Get -> when (request.url) {
                    input -> respondRedirect(TARGET)
                    else -> respondOk()
                }

                else -> fail("Expected either Head or Get", actual = request.method)
            }
        }

        val client = HttpClient(mockEngine)
        val result = resolveLocal(client, input)

        assertSuccess(result)
            .isInstanceOf<ResolveResultType.Resolved.Local>()
            .transform { it.url }
            .isEqualTo(TARGET)
    }

    @Test
    fun `redirect loop`(): Unit = runBlocking {
        val mockEngine = MockEngine { _ ->
            respondRedirect(TARGET)
        }

        val client = HttpClient(mockEngine)
        val result = resolveLocal(client, input)

        assertFailure(result).isInstanceOf<SendCountExceedException>()
    }

    @After
    fun teardown() = stopKoin()
}
