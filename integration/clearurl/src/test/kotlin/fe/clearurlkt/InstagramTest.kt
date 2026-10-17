package fe.clearurlkt

import assertk.assertThat
import assertk.assertions.isEqualTo
import de.infix.testBalloon.framework.core.testSuite
import fe.clearurlskt.ClearUrls
import fe.clearurlskt.provider.ComplexRule
import fe.clearurlskt.provider.LengthExactMatchCondition
import fe.clearurlskt.provider.Matcher
import fe.clearurlskt.provider.ProviderV2
import fe.clearurlskt.provider.RegexMatchCondition
import fe.clearurlskt.provider.RegexRule
import fe.clearurlskt.provider.RuleType
import fe.std.enum.enumFlagSet

val InstagramTestSuite by testSuite {
    val instagramProvider = ProviderV2(
        1,
        "instagram",
        """^https?:\/\/(?:[a-z0-9-]+\.)*?instagram\.com""".toRegex(),
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
    val clearUrls = ClearUrls(listOf(instagramProvider))
    val data = mapOf(
        "https://www.instagram.com/reel/7ldHSVPwG2x/?mdxt=SUU4YWxUWkNhT04K" to "https://www.instagram.com/reel/7ldHSVPwG2x/",
        "https://www.instagram.com/p/Iv9esfmIUhD/?utm_source=ig_web_copy_link&exln=a0VEOxSjVhZjcK==" to "https://www.instagram.com/p/Iv9esfmIUhD/?utm_source=ig_web_copy_link"
    )

    for ((input, expected) in data) {
        test("instagram") {
            val (result, _) = clearUrls.clearUrl(input)
            assertThat(result).isEqualTo(expected)
        }
    }
}
