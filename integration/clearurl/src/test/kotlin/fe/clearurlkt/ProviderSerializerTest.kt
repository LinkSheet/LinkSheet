package fe.clearurlkt

import assertk.all
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import de.infix.testBalloon.framework.core.testSuite
import fe.clearurlskt.provider.ComplexRule
import fe.clearurlskt.provider.LengthExactMatchCondition
import fe.clearurlskt.provider.Matcher
import fe.clearurlskt.provider.ProviderSerializer
import fe.clearurlskt.provider.ProviderV1
import fe.clearurlskt.provider.ProviderV2
import fe.clearurlskt.provider.RegexMatchCondition
import fe.clearurlskt.provider.RegexRule
import fe.clearurlskt.provider.RuleType
import fe.gson.extension.json
import fe.std.enum.enumFlagSet

val ProviderSerializerTest by testSuite {
    test("deserialize regular provider") {
        val json = """
        {
            "urlPattern": "^https?:\\/\\/(?:[a-z0-9-]+\\.)*?msn\\.com",
            "completeProvider": false,
            "rules": [
                "cvid",
                "ocid"
            ],
            "rawRules": [],
            "referralMarketing": [],
            "exceptions": [],
            "redirections": [],
            "forceRedirection": false
        }
        """.trimIndent().json.asJsonObject
        val result = ProviderSerializer.createProviderFromObject("msn", 1, json)
        val expected = ProviderV1(
            1,
            "msn",
            """^https?:\/\/(?:[a-z0-9-]+\.)*?msn\.com""".toRegex(),
            false,
            listOf("^cvid$".toRegex(), "^ocid$".toRegex()),
            rawRules = emptyList(),
            exceptions = emptyList(),
            referralMarketing = emptyList(),
            redirections = emptyList(),
        )

        assertThat(result).all {
            prop(ProviderV1::sortPosition).isEqualTo(expected.sortPosition)
            prop(ProviderV1::key).isEqualTo(expected.key)
            prop(ProviderV1::url).transform { it.toString() }.isEqualTo(expected.url.toString())
            prop(ProviderV1::completeProvider).isEqualTo(expected.completeProvider)
            prop(ProviderV1::rules)
                .transform { r -> r.map { it.toString() } }
                .containsExactly(*expected.rules.map { it.toString() }.toTypedArray())
            prop(ProviderV1::rawRules).isEmpty()
            prop(ProviderV1::exceptions).isEmpty()
            prop(ProviderV1::referralMarketing).isEmpty()
            prop(ProviderV1::redirections).isEmpty()
        }
    }
    test("deserialize extended rules provider") {
        val expected = ProviderV2(
            1,
            "instagram",
            """^https?:\\/\\/(?:[a-z0-9-]+\\.)*?instagram\\.com""".toRegex(),
            false,
            rules = listOf(
                RegexRule(
                    ruleType = enumFlagSet(RuleType.QueryParameter, RuleType.Fragment),
                    regex = "^igshid$".toRegex()
                ),
                RegexRule(
                    ruleType = enumFlagSet(RuleType.QueryParameter, RuleType.Fragment),
                    regex = "^igsh$".toRegex()
                ),
                RegexRule(
                    ruleType = enumFlagSet(RuleType.QueryParameter, RuleType.Fragment),
                    regex = "^igsi$".toRegex()
                ),
                ComplexRule(
                    ruleType = enumFlagSet(RuleType.QueryParameter),
                    keyMatcher = Matcher(
                        conditions = listOf(
                            LengthExactMatchCondition(4),
                            RegexMatchCondition("[a-z]{4}".toRegex())
                        )
                    ),
                    valueMatcher = Matcher(
                        conditions = listOf(
                            LengthExactMatchCondition(16),
                            RegexMatchCondition("[A-Za-z0-9-_]{14,16}={0,2}".toRegex())
                        )
                    )
                )
            ),
            rawRules = emptyList(),
            exceptions = emptyList(),
            referralMarketing = emptyList(),
            redirections = listOf(""".*u=([^&]*)""".toRegex()),
        )

//        assertThat(result).all {
//            prop(ProviderV1::sortPosition).isEqualTo(expected.sortPosition)
//            prop(ProviderV1::key).isEqualTo(expected.key)
//            prop(ProviderV1::url).transform { it.toString() }.isEqualTo(expected.url.toString())
//            prop(ProviderV1::completeProvider).isEqualTo(expected.completeProvider)
//            prop(ProviderV1::rules)
//                .transform { r -> r.map { it.toString() } }
//                .containsExactly(*expected.rules.map { it.toString() }.toTypedArray())
//            prop(ProviderV1::rawRules).isEmpty()
//            prop(ProviderV1::exceptions).isEmpty()
//            prop(ProviderV1::referralMarketing).isEmpty()
//            prop(ProviderV1::redirections).isEmpty()
//        }
    }
}
