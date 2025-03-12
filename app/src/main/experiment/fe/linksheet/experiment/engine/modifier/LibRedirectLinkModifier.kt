package fe.linksheet.experiment.engine.modifier

import fe.linksheet.module.resolver.LibRedirectResolver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LibRedirectLinkModifier(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val resolver: LibRedirectResolver,
    private val useJsEngine: () -> Boolean = { false }
) : LinkModifier {
    override suspend fun warmup() = withContext(ioDispatcher) {
        resolver.warmup()
    }

    override suspend fun modify(data: ModifyInput): ModifyOutput? = withContext(ioDispatcher) {
//        val ignoreLibRedirectExtra = intent.getBooleanExtra(LibRedirectDefault.libRedirectIgnore, false)
//        if (ignoreLibRedirectExtra) {
//            intent.extras?.remove(LibRedirectDefault.libRedirectIgnore)
//        }

//        if (ignoreLibRedirectExtra && ignoreLibRedirectButton) return@withContext null

        resolver.resolve(data.url, useJsEngine())
        ModifyOutput(data.url)
    }
}
