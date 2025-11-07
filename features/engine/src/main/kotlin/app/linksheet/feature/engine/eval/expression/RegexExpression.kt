@file:OptIn(ExperimentalSerializationApi::class)

package app.linksheet.feature.engine.eval.expression

import androidx.annotation.Keep
import app.linksheet.feature.engine.eval.EvalContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Keep
@Serializable
@SerialName(OpCodes.REGEX)
internal class RegexExpression(
    @ProtoNumber(1)
    val expression: Expression<String>
) : Expression<Regex> {
    override fun eval(ctx: EvalContext): Regex {
        return Regex(expression.eval(ctx))
    }
}

@Keep
@Serializable
@SerialName(OpCodes.REGEX_MATCH_ENTIRE)
internal class RegexMatchEntireExpression(
    @ProtoNumber(1)
    val regex: Expression<@Contextual Regex>,
    @ProtoNumber(2)
    val string: Expression<String>
) : Expression<Boolean> {
    override fun eval(ctx: EvalContext): Boolean {
        return regex.eval(ctx).matchEntire(string.eval(ctx)) != null
    }
}

