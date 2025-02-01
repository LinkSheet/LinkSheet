package fe.linksheet.experiment.engine.modifier

import fe.linksheet.module.resolver.LibRedirectResolver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LibRedirectLinkModifier(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val resolver: LibRedirectResolver,
    private val jsEngine: () -> Boolean = { false }
) : LinkModifier {
    override suspend fun modify(data: ModifyInput): ModifyOutput? = withContext(dispatcher) {
//        val ignoreLibRedirectExtra = intent.getBooleanExtra(LibRedirectDefault.libRedirectIgnore, false)
//        if (ignoreLibRedirectExtra) {
//            intent.extras?.remove(LibRedirectDefault.libRedirectIgnore)
//        }

//        if (ignoreLibRedirectExtra && ignoreLibRedirectButton) return@withContext null

        resolver.resolve(data.url, jsEngine())
        ModifyOutput(data.url)
    }
}
