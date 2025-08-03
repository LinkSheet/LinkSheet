package fe.linksheet.experiment.engine.modifier

import fe.embed.resolve.EmbedResolver
import fe.embed.resolve.loader.BundledEmbedResolveConfigLoader
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.experiment.engine.step.StepResult
import fe.std.uri.StdUrl
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EmbedLinkModifier(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LinkModifier<EmbedLinkModifyOutput> {
    override val id = EngineStepId.Embed

    companion object {
        private val embedResolverConfig by lazy { BundledEmbedResolveConfigLoader.load().getOrNull() }
    }

    private val embedResolver by lazy { embedResolverConfig?.let { EmbedResolver(it) } }

    override suspend fun warmup() = withContext(ioDispatcher) {
        embedResolver
        Unit
    }

    override suspend fun EngineRunContext.runStep(url: StdUrl) = withContext(ioDispatcher) {
        val result = embedResolver?.resolve(url.toString())
        result?.let { EmbedLinkModifyOutput(it.toStdUrlOrThrow()) }
    }
}

data class EmbedLinkModifyOutput(override val url: StdUrl) : StepResult
