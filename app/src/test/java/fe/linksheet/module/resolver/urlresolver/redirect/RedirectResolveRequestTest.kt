package fe.linksheet.module.resolver.urlresolver.redirect

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.prop
import fe.httpkt.Request
import fe.linksheet.module.resolver.urlresolver.RealCachedRequest
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.testlib.core.BaseUnitTest
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class RedirectResolveRequestTest : BaseUnitTest {
    companion object {
        private val request = Request()
        private val cachedRequest = RealCachedRequest(request)
    }

    @org.junit.Test
    fun `test refresh header is redirect`() = runTest {
        val client = HttpClient(MockEngine {
            if (it.method == HttpMethod.Head) {
                return@MockEngine respond(
                    byteArrayOf(),
                    headers = headersOf("refresh", "0;url=https://linkin.bio/google")
                )
            }

            throw IllegalStateException()
        })


        val request = RedirectResolveRequest(client)

        // This is a dummy, we mock the response above
        val url = "https://l.instagram.com/?u=https%3A%2F%2Flinkin.bio%2Fgoogle&e=123"
        val result = request.resolveLocal(url, 0).getOrNull()

        assertThat(result)
            .isNotNull()
            .isInstanceOf<ResolveResultType.Resolved.Local>()
            .prop(ResolveResultType.Resolved.Local::url)
            .isEqualTo("https://linkin.bio/google")
    }
}
