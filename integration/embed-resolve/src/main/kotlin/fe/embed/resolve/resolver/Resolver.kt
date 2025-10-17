package fe.embed.resolve.resolver

import fe.embed.resolve.config.Config

public sealed interface Resolver<in T : Config> {
    public fun resolve(uriString: String, config: T): String?
}
