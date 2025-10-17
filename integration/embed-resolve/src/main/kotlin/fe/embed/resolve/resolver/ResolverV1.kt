package fe.embed.resolve.resolver

import fe.embed.resolve.config.ConfigV1
import fe.embed.resolve.config.ServiceV1
import fe.std.result.isFailure
import fe.std.uri.StdUrlFactory
import org.jetbrains.annotations.VisibleForTesting

public object ResolverV1 : Resolver<ConfigV1> {
    override fun resolve(uriString: String, config: ConfigV1): String? {
        val result = StdUrlFactory.parse(uriString)
        if (result.isFailure()) return null

        val url = result.value
        val host = url.host
        val service = config.services.firstOrNull { host in it.embedDomains } ?: return null

        val path = url.pathString
        if (service.ignorePattern?.matches(path) == true) return null

        val (_, relevantPath) = isMatch(service, path) ?: return null
        return "${url.scheme}://${service.domain}$relevantPath"
    }

    @VisibleForTesting
    public fun isMatch(service: ServiceV1, path: String): List<String>? {
        val match = service.pattern.matchEntire(path) ?: return null
        return match.groupValues
    }
}
