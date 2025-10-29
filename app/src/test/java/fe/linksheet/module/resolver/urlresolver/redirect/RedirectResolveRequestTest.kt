package fe.linksheet.module.resolver.urlresolver.redirect

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.prop
import fe.httpkt.Request
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.file.DebugLogPersistService
import fe.linksheet.module.log.internal.DefaultLoggerDelegate
import fe.linksheet.module.redactor.LogHasher
import fe.linksheet.module.redactor.Redactor
import fe.linksheet.module.resolver.urlresolver.RealCachedRequest
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.testlib.core.BaseUnitTest
import okhttp3.OkHttpClient
import okhttp3.mock.MockInterceptor
import okhttp3.mock.head
import okhttp3.mock.rule
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class RedirectResolveRequestTest : BaseUnitTest  {
    companion object {
        private val loggerDelegate = DefaultLoggerDelegate(
            true,
            "test",
            Redactor(LogHasher.NoOpHasher),
            DebugLogPersistService()
        )
        private val logger = Logger(loggerDelegate)
        private val request = Request()
        private val cachedRequest = RealCachedRequest(request, logger)
    }

    @org.junit.Test
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
}
