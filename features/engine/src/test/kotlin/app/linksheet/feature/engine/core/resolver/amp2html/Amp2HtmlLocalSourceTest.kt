package app.linksheet.feature.engine.core.resolver.amp2html

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.util.withStatsTag
import fe.std.uri.toStdUrlOrThrow
import io.ktor.client.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class Amp2HtmlLocalSourceTest : BaseUnitTest {
    @org.junit.Test
    fun test() = runTest {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                // https://github.com/jaredsburrows/android-gif-search/pull/525
                withStatsTag(0xF00D) { chain.proceed(chain.request()) }
            })
//            .cache(cache)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()

        val client = HttpClient(OkHttp) {
            engine { preconfigured = okHttpClient }
            install(ContentNegotiation) { gson() }
            install(ContentEncoding) {
                deflate(1.0F)
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

        val source = Amp2HtmlLocalSource(client)


        val url = "https://github.com/Kotlin/binary-compatibility-validator"
        val result = source.resolve(url.toStdUrlOrThrow())

    }
}
