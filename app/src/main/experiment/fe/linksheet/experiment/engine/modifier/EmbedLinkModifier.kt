package fe.linksheet.experiment.engine.modifier

import fe.embed.resolve.EmbedResolver
import fe.embed.resolve.loader.BundledEmbedResolveConfigLoader

class EmbedLinkModifier : LinkModifier {
    companion object {
        private val embedResolverConfig by lazy { BundledEmbedResolveConfigLoader.load().getOrNull() }
    }

    private val embedResolver = embedResolverConfig?.let { EmbedResolver(it) }

    override suspend fun modify(data: ModifyInput): ModifyOutput? {
        val result = embedResolver?.resolve(data.url)
        return result?.let { ModifyOutput(it) }
    }
}
