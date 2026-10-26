package fe.clearurlskt


import fe.clearurlskt.Modification.ParameterRemoval
import fe.clearurlskt.Modification.RawRule
import fe.clearurlskt.Modification.Redirection
import fe.clearurlskt.provider.Provider
import fe.clearurlskt.util.keyValueMapToString
import fe.clearurlskt.util.toFragmentMap
import fe.relocated.org.apache.hc.core5.core5.net.PercentCodec
import fe.std.result.isFailure
import fe.std.uri.EncodingPolicy
import fe.std.uri.StdUrl
import fe.std.uri.StdUrlFactory
import fe.std.uri.extension.buildUrl

public class ClearUrls(
    private val providers: List<Provider>,
    private val skipUnchanged: () -> Boolean = { true },
) {
    private suspend fun SequenceScope<ClearUrlOperation>.isException(
        provider: Provider,
        editUrl: String
    ): Boolean {
        for (regex in provider.exceptions) {
            if (!regex.containsMatchIn(editUrl)) continue

            yield(Exception(provider.key, editUrl, regex))
            return true
        }

        return false
    }

    private suspend fun SequenceScope<ClearUrlOperation>.handleRedirections(
        provider: Provider,
        url: String,
        settings: ClearUrlSettings
    ): String? {
        for (regex in provider.redirections) {
            val result = regex.find(url) ?: continue

            val (_, redirect) = result.groupValues

            val resultUrl = PercentCodec.decode(redirect, Charsets.UTF_8)
            if (!resultUrl.contains("://")) {
                return StdUrlFactory.fixHttpUrl(resultUrl)
            }

            yield(Redirection(provider.key, url, resultUrl, regex))
            return clearUrl(resultUrl.toString(), settings)
        }

        return null
    }

    private suspend fun SequenceScope<ClearUrlOperation>.applyRawRules(
        provider: Provider,
        url: String
    ): String {
        var mutUrl = url
        for (regex in provider.rawRules) {
            val preReplaceUrl = mutUrl
            mutUrl = mutUrl.replace(regex, "")

            if (preReplaceUrl == mutUrl) continue
            yield(RawRule(provider.key, preReplaceUrl, mutUrl, regex))
        }
        return mutUrl
    }

    private fun MutableList<Pair<String, String?>>.removeIfKey(predicate: (String) -> Boolean): MutableSet<String> {
        val removeKeys = mutableSetOf<String>()
        for (pair in this) {
            if (!predicate(pair.first)) continue
            removeKeys.add(pair.first)
        }

        for (key in removeKeys) {
            removeIf { key == it.first }
        }

        return removeKeys
    }

    private fun <K, V> MutableMap<K, V>.removeIfKey(predicate: (K) -> Boolean): MutableSet<K> {
        val removeKeys = mutableSetOf<K>()
        for (key in keys) {
            if (!predicate(key)) continue
            removeKeys.add(key)
        }

        for (key in removeKeys) {
            remove(key)
        }

        return removeKeys
    }

    private suspend fun SequenceScope<ClearUrlOperation>.applyRules(
        provider: Provider,
        url: StdUrl,
        settings: ClearUrlSettings
    ): String? {
        val fields = url.queryParams.toMutableList()
        val fragments = url.toFragmentMap()

        val fieldChanges = mutableSetOf<String>()
        val fragmentChanges = mutableSetOf<String>()

        if (fields.isEmpty() && fragments.isEmpty()) return null

        for (rule in provider.rules) {
            fieldChanges.addAll(fields.removeIfKey { rule.containsMatchIn(it) })
            fragmentChanges.addAll(fragments.removeIfKey { rule.containsMatchIn(it) })
        }
        if (settings.skipUnchanged) {
            if (fieldChanges.isEmpty() && fragmentChanges.isEmpty()) return null
        }

        val inputUrl = url.toString()
        val url = buildUrl(encodingPolicy = EncodingPolicy.RFC_3986) {
            scheme = url.scheme
            host = url.host
            port = url.port
            userInfo = url.userInfo
            pathSegments = url.pathSegments
            fragment = fragments.keyValueMapToString().takeIf { it.isNotEmpty() }
            setParameters(fields)
        }

        val resultUrl = url.toString()
        yield(ParameterRemoval(provider.key, inputUrl, resultUrl, fieldChanges, fragmentChanges))

        return resultUrl
    }

    private suspend fun SequenceScope<ClearUrlOperation>.clearUrl(
        url: String,
        settings: ClearUrlSettings
    ): String {
        var mutUrl = url.trim()
        for (provider in providers) {
            if (!provider.url.containsMatchIn(mutUrl)) continue

            if (isException(provider, mutUrl)) {
                return mutUrl
            }

            val redirectionUrl = handleRedirections(provider, mutUrl, settings)
            if (redirectionUrl != null) {
                return redirectionUrl
            }

            mutUrl = applyRawRules(provider, mutUrl)

            val parseResult = StdUrlFactory.parse(
                mutUrl,
                encodingPolicy = EncodingPolicy.RFC_3986,
                setPlusAsBlanks = true
            )
            if (parseResult.isFailure()) {
                yield(ParseFailure(provider.key, mutUrl, parseResult))
                return mutUrl
            }

            mutUrl = applyRules(provider, parseResult.value, settings) ?: continue
        }

        return mutUrl
    }

    public fun clearUrl(
        url: String,
        settings: ClearUrlSettings
    ): Pair<String, List<ClearUrlOperation>> {
        var result = url
        val operations = sequence<ClearUrlOperation> { result = clearUrl(url, settings) }.toList()
        return result to operations
    }
}

public data class ClearUrlSettings(
    val skipUnchanged: Boolean = true
)
