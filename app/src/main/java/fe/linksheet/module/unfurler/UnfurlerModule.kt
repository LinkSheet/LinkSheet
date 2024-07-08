package fe.linksheet.module.unfurler

import me.saket.unfurl.Unfurler
import okhttp3.OkHttpClient
import org.koin.dsl.module

val unfurlerModule = module {
    single<Unfurler> {
        Unfurler(cacheSize = 500, httpClient = get<OkHttpClient>())
    }
}
