package fe.linksheet.module.resolver.urlresolver.redirect

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.prop
import assertk.tableOf
import fe.httpkt.Request
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.file.DebugLogPersistService
import fe.linksheet.module.log.internal.DefaultLoggerDelegate
import fe.linksheet.module.redactor.LogHasher
import fe.linksheet.module.redactor.Redactor
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.testlib.core.RobolectricTest
import okhttp3.OkHttpClient
import okhttp3.mock.MockInterceptor
import okhttp3.mock.head
import okhttp3.mock.rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class RedirectResolveRequestTest : RobolectricTest {
    companion object {
        private val loggerDelegate = DefaultLoggerDelegate(
            true,
            "test",
            Redactor(LogHasher.NoOpHasher),
            DebugLogPersistService()
        )
        private val logger = Logger(loggerDelegate)
        private val request = Request()
        private val cachedRequest = CachedRequest(request, logger)
    }

    @Test
    fun `test refresh header is redirect`() {
        val interceptor = MockInterceptor().apply {
            rule(head) {
                respond(200).header("refresh", "0;url=https://linkin.bio/google")
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val request = RedirectResolveRequest("dummy", "dummy", request, cachedRequest, client)

        // This is a dummy, we mock the response above
        val url = "https://l.instagram.com/?u=https%3A%2F%2Flinkin.bio%2Fgoogle&e=123"
        val result = request.resolveLocal(url, 0).getOrNull()

        assertThat(result)
            .isNotNull()
            .isInstanceOf<ResolveResultType.Resolved.Local>()
            .prop(ResolveResultType.Resolved.Local::url)
            .isEqualTo("https://linkin.bio/google")
    }

    @Test
    fun `test parse valid refresh header`() {
        tableOf("header", "expected")
            .row("0,https://linkin.bio/google", 0 to "https://linkin.bio/google")
            .row("0;https://linkin.bio/google", 0 to "https://linkin.bio/google")
            .row("0;URL=https://linkin.bio/google", 0 to "https://linkin.bio/google")
            .row("1;URL=https://linkin.bio/google", 1 to "https://linkin.bio/google")
            .row("1.1;URL=https://linkin.bio/google", 1 to "https://linkin.bio/google")
            .row("0;url=https://linkin.bio/google", 0 to "https://linkin.bio/google")
            .row("0;url=\"https://linkin.bio/google\"", 0 to "https://linkin.bio/google")
            .row("0;url='https://linkin.bio/google'", 0 to "https://linkin.bio/google")
            // Parser doesn't do validation beyond basic length checks, so this is valid
            .row("0;url=ht", 0 to "ht")
            .forAll { header, expected ->
                assertThat(RedirectResolveRequest.parseRefreshHeader(header)).isEqualTo(expected)
            }
    }

    @Test
    fun `test parse invalid refresh header`() {
        tableOf("header", "expected")
            .row<String, Pair<Int?, String?>?>("1.1", null)
            .row("1.1;", null)
            .row("1", null)
            .row("1;", null)
            .row(";", null)
            .row(";;;", null)
            .row(";URL=", null)
            .row(";url=", null)
            .row("; url=", null)
            .row("; url =", null)
            .row(";;url=", null)
            .forAll { header, expected ->
                assertThat(RedirectResolveRequest.parseRefreshHeader(header)).isEqualTo(expected)
            }
    }
}
