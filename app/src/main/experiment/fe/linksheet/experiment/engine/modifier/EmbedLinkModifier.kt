package fe.linksheet.experiment.engine.modifier

import fe.embed.resolve.EmbedResolver
import fe.embed.resolve.loader.BundledEmbedResolveConfigLoader
import fe.linksheet.experiment.engine.LinkModifier
import fe.linksheet.experiment.engine.ModifyInput
import fe.linksheet.experiment.engine.ModifyOutput

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
