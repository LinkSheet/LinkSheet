package app.linksheet.feature.clearurls.core

import fe.clearurlskt.ClearUrlOperation
import fe.clearurlskt.ClearUrlSettings
import fe.clearurlskt.ClearUrls
import fe.clearurlskt.Modification.ParameterRemoval
import fe.clearurlskt.config.provider.Provider
import fe.std.result.getOrNull
import fe.std.uri.EncodingPolicy
import fe.std.uri.StdUrlFactory

/** Applies the fork's explicit provider overrides after upstream has resolved redirects. */
class ConfiguredClearUrls(private val providers: List<Provider>) {
    private val cleaner = ClearUrls(providers)

    fun clearUrl(
        url: String,
        settings: ClearUrlSettings = ClearUrlSettings(),
        removeAllQueryProviders: Set<String> = emptySet(),
    ): Pair<String, List<ClearUrlOperation>> {
        val (result, operations) = cleaner.clearUrl(url, settings)
        // Match the destination, including exceptions, without stripping redirect arguments early.
        val provider = providers.firstOrNull {
            it.key in removeAllQueryProviders && it.url.containsMatchIn(result)
        } ?: return result to operations
        val stripped = removeAllQueryParameters(result)
        if (stripped == result) return result to operations

        val fields = StdUrlFactory.parse(result, encodingPolicy = EncodingPolicy.RFC_3986)
            .getOrNull()?.queryParams?.mapTo(mutableSetOf()) { it.first } ?: mutableSetOf()
        return stripped to operations + ParameterRemoval(provider.key, result, stripped, fields, mutableSetOf())
    }
}
