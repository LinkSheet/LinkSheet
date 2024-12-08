package fe.linksheet.experiment.engine.modifier

import fe.embed.resolve.EmbedResolver
import fe.embed.resolve.config.ConfigType
import fe.linksheet.experiment.engine.ModifyInput
import fe.linksheet.experiment.engine.LinkModifier
import fe.linksheet.experiment.engine.ModifyOutput

class EmbedLinkModifier : LinkModifier {
    companion object {
        private val embedResolverBundled by lazy { ConfigType.Bundled.load() }
    }

    override suspend fun modify(data: ModifyInput): ModifyOutput? {
        val result = EmbedResolver.resolve(data.url, embedResolverBundled)
        return result?.let { ModifyOutput(it) }
    }
}
