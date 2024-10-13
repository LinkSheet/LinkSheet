package fe.linksheet.module.http

import android.content.Context
import fe.httpkt.Request
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.dsl.module


val HttpClientModule = module {
    // TODO: Do we actually need this as a DI singleton?
    single<Request> {
        Request {
            headers {
                "Accept" += "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8"
                "Accept-Encoding" += "gzip, deflate"
                "Accept-Language" += "en-US,en;q=0.5"
                "Cache-Control" += "no-cache"
                "Connection" += "keep-alive"
                "Pragma" += "no-cache"
                "Sec-Fetch-Dest" += "document"
                "Sec-Fetch-Mode" += "navigate"
                "Sec-Fetch-Site" += "none"
                "Sec-Fetch-User" += "?1"
                "Upgrade-Insecure-Requests" += "1"
                "User-Agent" += "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36"
            }
        }
    }
    single<OkHttpClient> {
        val context = get<Context>()
        val cache = Cache(context.cacheDir, 25 * 1024 * 1024)

        OkHttpClient.Builder().cache(cache).build()
    }

    single<HttpClient> {
        val client = get<OkHttpClient>()

        HttpClient(OkHttp) {
            engine {
                preconfigured = client
            }
        }
    }
}
