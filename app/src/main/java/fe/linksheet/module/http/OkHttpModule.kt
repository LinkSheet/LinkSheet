package fe.linksheet.module.http

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.dsl.module


val okHttpModule = module {
    single<OkHttpClient> {
        val context = get<Context>()
        val cache = Cache(context.cacheDir, 25 * 1024 * 1024)

        OkHttpClient.Builder().cache(cache).build()
    }
}
