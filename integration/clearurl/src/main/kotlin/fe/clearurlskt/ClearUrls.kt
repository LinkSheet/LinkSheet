package fe.clearurlskt


import fe.clearurlskt.Modification.ParameterRemoval
import fe.clearurlskt.Modification.RawRule
import fe.clearurlskt.Modification.Redirection
import fe.clearurlskt.provider.ComplexRule
import fe.clearurlskt.provider.LengthExactMatchCondition
import fe.clearurlskt.provider.Matcher
import fe.clearurlskt.provider.Provider
import fe.clearurlskt.provider.ProviderV1
import fe.clearurlskt.provider.ProviderV2
import fe.clearurlskt.provider.RegexMatchCondition
import fe.clearurlskt.provider.RegexRule
import fe.clearurlskt.provider.RuleType
import fe.clearurlskt.provider.RuleV2
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
        url: String
    ): String? {
        for (regex in provider.redirections) {
            val result = regex.find(url) ?: continue

            val (_, redirect) = result.groupValues

            val resultUrl = PercentCodec.decode(redirect, Charsets.UTF_8)
            if (!resultUrl.contains("://")) {
                return StdUrlFactory.fixHttpUrl(resultUrl)
            }

            yield(Redirection(provider.key, url, resultUrl, regex))
            return clearUrl(resultUrl.toString())
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

    private fun MutableList<Pair<String, String?>>.removeIf(predicate: (String, String?) -> Boolean): MutableSet<String> {
        val removeKeys = mutableSetOf<String>()
        for ((key, value) in this) {
            if (!predicate(key, value)) continue
            removeKeys.add(key)
        }

        removeAll { (k, _) -> k in removeKeys }
        return removeKeys
    }

    private fun <K, V> MutableMap<K, V>.removeIf(predicate: (K, V) -> Boolean): MutableSet<K> {
        val removeKeys = mutableSetOf<K>()
        for ((key, value) in this) {
            if (!predicate(key, value)) continue
            removeKeys.add(key)
            remove(key)
        }

        return removeKeys
    }

    private suspend fun SequenceScope<ClearUrlOperation>.applyRules(
        provider: Provider,
        url: StdUrl
    ): String? {
        val fields = url.queryParams.toMutableList()
        val fragments = url.toFragmentMap()

        val fieldChanges = mutableSetOf<String>()
        val fragmentChanges = mutableSetOf<String>()

        if (fields.isEmpty() && fragments.isEmpty()) return null

        if (provider is ProviderV1) {
            for (rule in provider.rules) {
                fieldChanges.addAll(fields.removeIf { key, _ -> rule.containsMatchIn(key) })
                fragmentChanges.addAll(fragments.removeIf { key, _ -> rule.containsMatchIn(key) })
            }
        } else if (provider is ProviderV2) {
            for (rule in provider.rules) {
                if (RuleType.QueryParameter in rule.ruleType) {
                    val changes = fields.removeIf { k, v -> matchRuleV2(rule, k, v) }
                    fieldChanges.addAll(changes)
                }
                if (RuleType.Fragment in rule.ruleType) {
                    val changes = fragments.removeIf { k, v -> matchRuleV2(rule, k, v) }
                    fragmentChanges.addAll(changes)
                }
            }
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

    private fun matchRuleV2(rule: RuleV2, key: String, value: String?): Boolean {
        if (rule is RegexRule) {
            return rule.regex.containsMatchIn(key)
        }
        if (rule !is ComplexRule) {
            return false
        }

        fun handleMatcher(matcher: Matcher, str: String): Boolean {
            for (condition in matcher.conditions) {
                when (condition) {
                    is LengthExactMatchCondition if (str.length != condition.length) -> {
                        return false
                    }

                    is RegexMatchCondition if (!condition.regex.containsMatchIn(str)) -> {
                        return false
                    }

                    else -> {
                        // fine, continue
                    }
                }
            }
            return true
        }

        if (rule.keyMatcher != null && !handleMatcher(rule.keyMatcher, key)) {
            return false
        }

        // TODO: is it a good idea to treat absent values as an empty string? (values are only ever null for fragments)
        if (rule.valueMatcher != null && !handleMatcher(rule.valueMatcher, value ?: "")) {
            return false
        }

        return true
    }

    private suspend fun SequenceScope<ClearUrlOperation>.clearUrl(url: String): String {
        var mutUrl = url.trim()
        for (provider in providers) {
            if (!provider.url.containsMatchIn(mutUrl)) continue

            if (isException(provider, mutUrl)) {
                return mutUrl
            }

            val redirectionUrl = handleRedirections(provider, mutUrl)
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

            mutUrl = applyRules(provider, parseResult.value) ?: continue
        }

        return mutUrl
    }

    public fun clearUrl(url: String): Pair<String, List<ClearUrlOperation>> {
        var result = url
        val operations = sequence<ClearUrlOperation> { result = clearUrl(url) }.toList()
        return result to operations
    }
}


