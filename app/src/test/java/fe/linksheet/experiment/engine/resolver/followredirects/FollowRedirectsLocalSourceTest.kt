package fe.linksheet.experiment.engine.resolver.followredirects

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import assertk.fail
import fe.linksheet.UnitTest
import fe.std.result.assert.assertFailure
import fe.std.result.assert.assertSuccess
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class FollowRedirectsLocalSourceTest : UnitTest {
    companion object {
        private const val INPUT = "https://linksheet.app/redirect-me"
        private const val TARGET = "https://linksheet.app/target"
    }

    @Test
    fun `redirect on head request`(): Unit = runTest {
        val mockEngine = MockEngine { request ->
            when (request.url.toString()) {
                INPUT -> respondRedirect(TARGET)
                else -> respondOk()
            }
        }

        val client = HttpClient(mockEngine) {
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 1000
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
            }
        }

        val source = FollowRedirectsLocalSource(client)
        val result = source.resolve(INPUT)

        assertSuccess(result)
            .isInstanceOf<FollowRedirectsResult.LocationHeader>()
            .prop(FollowRedirectsResult.LocationHeader::url)
            .isEqualTo(TARGET)
    }

    @Test
    fun `return 400 on head, redirect on get`(): Unit = runTest {
        val mockEngine = MockEngine { request ->
            when (request.method) {
                HttpMethod.Head -> respondBadRequest()
                HttpMethod.Get -> when (request.url.toString()) {
                    INPUT -> respondRedirect(TARGET)
                    else -> respondOk()
                }

                else -> fail("Expected either Head or Get", actual = request.method)
            }
        }

        val source = FollowRedirectsLocalSource(HttpClient(mockEngine))
        val result = source.resolve(INPUT)

        assertSuccess(result)
            .isInstanceOf<FollowRedirectsResult.GetRequest>()
            .prop(FollowRedirectsResult.GetRequest::url)
            .isEqualTo(TARGET)
    }

    @Test
    fun `redirect loop`(): Unit = runTest {
        val mockEngine = MockEngine { _ ->
            respondRedirect(TARGET)
        }

        val source = FollowRedirectsLocalSource(HttpClient(mockEngine))
        val result = source.resolve(INPUT)

        assertFailure(result).isInstanceOf<SendCountExceedException>()
    }
}
