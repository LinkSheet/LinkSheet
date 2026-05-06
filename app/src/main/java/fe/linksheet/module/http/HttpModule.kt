package fe.linksheet.module.http

import android.content.Context
import android.net.TrafficStats
import app.linksheet.api.CachedRequest
import app.linksheet.lib.http.DefaultHeaders
import app.linksheet.lib.http.TaggedRequest
import app.linksheet.util.buildconfig.StaticBuildInfo
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import fe.httpkt.Request
import fe.linksheet.module.resolver.urlresolver.RealCachedRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.gson.gson
import me.saket.unfurl.Unfurler
import okhttp3.Cache
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.koin.dsl.module
import java.net.InetSocketAddress
import java.net.Proxy

val HttpModule = module {
    // TODO: Do we actually need this as a DI singleton?
    single<Request> {
        TaggedRequest { addHeaders(DefaultHeaders) }
    }
    single<OkHttpClient> {
        val context = get<Context>()
        val cache = Cache(context.cacheDir, 25 * 1024 * 1024)

        OkHttpClient.Builder()
            // Via https://github.com/jaredsburrows/android-gif-search/blob/19ea35435e0962cd7d419a4ee02b05f5cebdb6e6/app/src/main/java/com/burrowsapps/gif/search/di/NetworkModule.kt#L90
            .eventListener(eventListener = object : EventListener() {
                override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
                    TrafficStats.setThreadStatsTag(0xF00D)
                }

                override fun connectEnd(
                    call: Call,
                    inetSocketAddress: InetSocketAddress,
                    proxy: Proxy,
                    protocol: Protocol?
                ) {
                    TrafficStats.clearThreadStatsTag()
                }
            })
//            .addInterceptor(Interceptor { chain ->
//                // https://github.com/jaredsburrows/android-gif-search/pull/525
//                withStatsTag(0xF00D) { chain.proceed(chain.request()) }
//            })
            .cache(cache)
            // Default
//            .followRedirects(true)
//            .followSslRedirects(true)
            .build()
    }
    single<HttpClient> {
        HttpClient(OkHttp) {
            engine { preconfigured = get<OkHttpClient>() }
            install(ContentNegotiation) { gson() }
            install(HttpTimeout) {
                // TODO: Is this a good idea?
                requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
            }
        }
    }
    single<ImageLoader> {
        val context = get<Context>()
        val okHttp = get<OkHttpClient>()
        // There's probably a better way to do this
        val logger = if (StaticBuildInfo.IsDebug) CoilLoggerAdapter() else null
        ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory(callFactory = okHttp))
            }
            .logger(logger)
            .build()
    }
    single<Unfurler> { Unfurler(httpClient = get()) }
    single<CachedRequest> { RealCachedRequest(get()) }
}
