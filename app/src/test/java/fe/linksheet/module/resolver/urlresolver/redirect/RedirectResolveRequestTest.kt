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
import fe.linksheet.module.log.internal.DebugLoggerDelegate
import fe.linksheet.module.http.requestModule
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.resolver.urlresolver.cachedRequestModule
import fe.linksheet.util.KoinTestRuleFix
import okhttp3.OkHttpClient
import okhttp3.mock.MockInterceptor
import okhttp3.mock.head
import okhttp3.mock.rule
import org.junit.After
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.annotation.Config
import kotlin.intArrayOf
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class RedirectResolveRequestTest : KoinTest {
    @get:Rule
    val koinTestRule = KoinTestRuleFix.create {
        modules(
            DebugLoggerDelegate.Factory,
            requestModule,
            cachedRequestModule,
        )
    }

    private val request by inject<Request>()
    private val cachedRequest by inject<CachedRequest>()

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

    @After
    fun teardown() = stopKoin()
}
