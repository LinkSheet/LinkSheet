package fe.linksheet.experiment.engine.modifier

import android.net.Uri
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.resolver.LibRedirectResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LibRedirectLinkModifier(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val resolver: LibRedirectResolver,
    private val useJsEngine: () -> Boolean = { false }
) : LinkModifier<LibRedirectModifyOutput> {

    override suspend fun warmup() = withContext(ioDispatcher) {
        resolver.warmup()
    }

    override suspend fun modify(data: ModifyInput): LibRedirectModifyOutput = withContext(ioDispatcher) {
//        val ignoreLibRedirectExtra = intent.getBooleanExtra(LibRedirectDefault.libRedirectIgnore, false)
//        if (ignoreLibRedirectExtra) {
//            intent.extras?.remove(LibRedirectDefault.libRedirectIgnore)
//        }

//        if (ignoreLibRedirectExtra && ignoreLibRedirectButton) return@withContext null
        val jsEngine = useJsEngine()
        val result = resolver.resolve(data.url, jsEngine)
        result.toModifyOutput()
    }
}

sealed interface LibRedirectModifyOutput : ModifyResult {
    data class Redirected(val originalUri: Uri, val redirectedUri: Uri) : LibRedirectModifyOutput
    data object NotRedirected : LibRedirectModifyOutput
}

fun LibRedirectResult.toModifyOutput(): LibRedirectModifyOutput {
   return when(this) {
       is LibRedirectResult.Redirected -> LibRedirectModifyOutput.Redirected(originalUri, redirectedUri)
       LibRedirectResult.NotRedirected -> LibRedirectModifyOutput.NotRedirected
   }
}
