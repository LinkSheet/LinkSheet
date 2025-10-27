@file:OptIn(ExperimentalSerializationApi::class)

package app.linksheet.feature.engine.eval.expression

import androidx.annotation.Keep
import app.linksheet.feature.engine.eval.EvalContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Keep
@Serializable
@SerialName(OpCodes.STRING_EQUALS)
class StringEqualsExpression(
    @ProtoNumber(1)
    private val left: Expression<String?>,
    @ProtoNumber(2)
    private val right: Expression<String?>,
    @ProtoNumber(3)
    private val ignoreCase: Boolean
) : Expression<Boolean> {
    override fun eval(ctx: EvalContext): Boolean {
        return left.eval(ctx)?.equals(right.eval(ctx), ignoreCase) == true
    }
}

@Keep
@Serializable
@SerialName(OpCodes.STRING_CONTAINS)
class StringContainsExpression(
    @ProtoNumber(1)
    private val left: Expression<String?>,
    @ProtoNumber(2)
    private val right: Expression<String?>,
    @ProtoNumber(3)
    private val ignoreCase: Boolean
) : Expression<Boolean> {
    override fun eval(ctx: EvalContext): Boolean {
        val other = right.eval(ctx) ?: return false
        return left.eval(ctx)?.contains(other, ignoreCase) == true
    }
}
