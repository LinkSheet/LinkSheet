package fe.linksheet.module.http

import android.content.Context
import coil3.ImageLoader
import fe.httpkt.Request
import fe.linksheet.extension.koin.createLogger
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.util.buildconfig.Build
import fe.linksheet.util.withStatsTag
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.gson
import me.saket.unfurl.Unfurler
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.dsl.module

val HttpModule = module {
    // TODO: Do we actually need this as a DI singleton?
    single<Request> {
        TaggedRequest { addHeaders(DefaultHeaders) }
    }
    single<OkHttpClient> {
        val context = get<Context>()
        val cache = Cache(context.cacheDir, 25 * 1024 * 1024)

        OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                // https://github.com/jaredsburrows/android-gif-search/pull/525
                withStatsTag(0xF00D) { chain.proceed(chain.request()) }
            })
            .cache(cache)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }
    single<HttpClient> {
        HttpClient(OkHttp) {
            engine { preconfigured = get<OkHttpClient>() }
            install(ContentNegotiation) {
                gson()
            }
            install(ContentEncoding) {
                deflate(1.0F)
                gzip(0.9F)
            }
        }
    }
    single<ImageLoader> {
        // There's probably a better way to do this
        val logger = if (Build.IsDebug) CoilLoggerAdapter(logger = createLogger<ImageLoader>()) else null
        provideCoilImageLoader(get(), get(), logger)
    }
    single<Unfurler> { Unfurler(httpClient = get()) }
    single<CachedRequest> { CachedRequest(get(), createLogger<CachedRequest>()) }
}
