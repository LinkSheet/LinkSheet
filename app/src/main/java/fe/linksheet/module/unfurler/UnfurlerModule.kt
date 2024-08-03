package fe.linksheet.module.unfurler

import me.saket.unfurl.UnfurlResult
import me.saket.unfurl.Unfurler
import okhttp3.OkHttpClient
import org.koin.dsl.module

val unfurlerModule = module {
    single<CooperativeUnfurler> {
        CooperativeUnfurler(cacheSize = 500, httpClient = OkHttpClient())
    }
}

class CooperativeUnfurler(val cacheSize: Int, val httpClient: OkHttpClient) {
    private val unfurler = Unfurler(cacheSize, httpClient = httpClient)

    fun unfurl(url: String): UnfurlResult? {
        return unfurler.unfurl(url)
    }

    fun cancel() {
        httpClient.dispatcher.cancelAll()
    }
}
