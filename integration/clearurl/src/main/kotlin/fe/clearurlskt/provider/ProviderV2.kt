@file:OptIn(ExperimentalSerializationApi::class)

package fe.clearurlskt.provider

import com.google.errorprone.annotations.Keep
import fe.std.enum.EnumFlag
import fe.std.enum.EnumFlagSet
import fe.std.kotlinx.serialization.extension.RegexStringSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Keep
@Serializable
@SerialName("pv2")
public data class ProviderV2(
    @ProtoNumber(1)
    override val sortPosition: Int,
    @ProtoNumber(2)
    override val key: String,
    @ProtoNumber(3)
    @Serializable(with = RegexStringSerializer::class)
    override val url: Regex,
    @ProtoNumber(4)
    override val completeProvider: Boolean,
    @ProtoNumber(5)
    val rules: List<RuleV2>,
    @ProtoNumber(6)
    override val rawRules: List<@Serializable(with = RegexStringSerializer::class) Regex>,
    @ProtoNumber(7)
    override val referralMarketing: List<@Serializable(with = RegexStringSerializer::class) Regex>,
    @ProtoNumber(8)
    override val exceptions: List<@Serializable(with = RegexStringSerializer::class) Regex>,
    @ProtoNumber(9)
    override val redirections: List<@Serializable(with = RegexStringSerializer::class) Regex>,
) : Provider

@Serializable
public sealed interface RuleV2 {
    public val ruleType: EnumFlagSet<RuleType>
}

public enum class RuleType(public override val value: Long) : EnumFlag<RuleType> {
    QueryParameter(1 shl 1),
    Fragment(1 shl 2)
}

@Keep
@Serializable
@SerialName("rr")
public class RegexRule(
    @ProtoNumber(1)
//    @Serializable(with = EnumFlagSetSerializer::class)
    override val ruleType: @Contextual EnumFlagSet<RuleType>,
    @ProtoNumber(2)
    @Serializable(with = RegexStringSerializer::class)
    public val regex: Regex,
) : RuleV2 {

}

@Keep
@Serializable
@SerialName("cr")
public class ComplexRule(
    @ProtoNumber(1)
    override val ruleType: @Contextual EnumFlagSet<RuleType>,
    @ProtoNumber(2)
    public val keyMatcher: Matcher?,
    @ProtoNumber(3)
    public val valueMatcher: Matcher?
) : RuleV2 {
    init {
        check(keyMatcher != null || valueMatcher != null) { "Either keyMatcher or valueMatcher must be set!" }
    }

}

@Keep
@Serializable
@SerialName("m")
public class Matcher(
    @ProtoNumber(1)
    public val conditions: List<MatchCondition>
)

public sealed interface MatchCondition {

}


@Keep
@Serializable
@SerialName("lemc")
public class LengthExactMatchCondition(
    @ProtoNumber(1)
    public val length: Int
) : MatchCondition

@Keep
@Serializable
@SerialName("rmc")
public class RegexMatchCondition(
    @ProtoNumber(1)
    @Serializable(with = RegexStringSerializer::class)
    public val regex: Regex
) : MatchCondition
