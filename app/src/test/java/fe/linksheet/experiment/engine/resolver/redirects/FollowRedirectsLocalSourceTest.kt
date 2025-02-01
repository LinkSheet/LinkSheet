package fe.linksheet.experiment.engine.resolver.redirects

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.fail
import fe.linksheet.LinkSheetTest
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.std.result.assert.assertFailure
import fe.std.result.assert.assertSuccess
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class FollowRedirectsLocalSourceTest : LinkSheetTest {
    companion object {
        private const val input = "https://linksheet.app/redirect-me"
        private const val target = "https://linksheet.app/target"
    }

    @Test
    fun `redirect on head request`(): Unit = runBlocking {
        val mockEngine = MockEngine { request ->
            when (request.url.toString()) {
                input -> respondRedirect(target)
                else -> respondOk()
            }
        }

        val source = FollowRedirectsLocalSource(mockEngine)
        val result = source.resolve(input)

        assertSuccess(result)
            .isInstanceOf<FollowRedirectsResult.LocationHeader>()
            .transform { it.url }
            .isEqualTo(target)
    }

    @Test
    fun `return 400 on head, redirect on get`(): Unit = runBlocking {
        val mockEngine = MockEngine { request ->
            when (request.method) {
                HttpMethod.Head -> respondBadRequest()
                HttpMethod.Get -> when (request.url.toString()) {
                    input -> respondRedirect(target)
                    else -> respondOk()
                }

                else -> fail("Expected either Head or Get", actual = request.method)
            }
        }

        val source = FollowRedirectsLocalSource(mockEngine)
        val result = source.resolve(input)

        assertSuccess(result)
            .isInstanceOf<FollowRedirectsResult.GetRequest>()
            .transform { it.url }
            .isEqualTo(target)
    }

    @Test
    fun `redirect loop`(): Unit = runBlocking {
        val mockEngine = MockEngine { _ ->
            respondRedirect(target)
        }

        val source = FollowRedirectsLocalSource(mockEngine)
        val result = source.resolve(input)

        assertFailure(result).isInstanceOf<SendCountExceedException>()
    }
}
