package fe.linksheet.experiment.engine.modifier

import fe.embed.resolve.EmbedResolver
import fe.embed.resolve.loader.BundledEmbedResolveConfigLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EmbedLinkModifier(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : LinkModifier {
    companion object {
        private val embedResolverConfig by lazy { BundledEmbedResolveConfigLoader.load().getOrNull() }
    }

    private val embedResolver by lazy { embedResolverConfig?.let { EmbedResolver(it) } }

    override suspend fun warmup() = withContext(ioDispatcher) {
        embedResolver
        Unit
    }

    override suspend fun modify(data: ModifyInput): ModifyOutput? {
        val result = embedResolver?.resolve(data.url)
        return result?.let { ModifyOutput(it) }
    }
}
