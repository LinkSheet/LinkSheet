package fe.linksheet.experiment.engine.modifier

import fe.embed.resolve.EmbedResolver
import fe.embed.resolve.config.ConfigType
import fe.linksheet.experiment.engine.Input
import fe.linksheet.experiment.engine.LinkModifier
import fe.linksheet.experiment.engine.Output

class EmbedLinkModifier : LinkModifier {
    companion object {
        private val embedResolverBundled by lazy { ConfigType.Bundled.load() }
    }

    override fun modify(data: Input): Output? {
        val result = EmbedResolver.resolve(data.url, embedResolverBundled)
        return result?.let { Output(it) }
    }
}
