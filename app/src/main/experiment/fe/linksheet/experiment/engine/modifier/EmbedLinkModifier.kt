package fe.linksheet.experiment.engine.modifier

import fe.embed.resolve.EmbedResolver
import fe.embed.resolve.loader.BundledEmbedResolveConfigLoader
import fe.linksheet.experiment.engine.StepResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EmbedLinkModifier(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LinkModifier<EmbedLinkModifyOutput> {

    companion object {
        private val embedResolverConfig by lazy { BundledEmbedResolveConfigLoader.load().getOrNull() }
    }

    private val embedResolver by lazy { embedResolverConfig?.let { EmbedResolver(it) } }

    override suspend fun warmup() = withContext(ioDispatcher) {
        embedResolver
        Unit
    }

    override suspend fun run(url :String) = withContext(ioDispatcher) {
        val result = embedResolver?.resolve(url)
        result?.let { EmbedLinkModifyOutput(it) }
    }
}

data class EmbedLinkModifyOutput(override val url: String) : StepResult
