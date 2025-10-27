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
@SerialName(OpCodes.AND)
class AndExpression(
    @ProtoNumber(1)
    private val left: Expression<Boolean>,
    @ProtoNumber(2)
    private val right: Expression<Boolean>
) : Expression<Boolean> {
    override fun eval(ctx: EvalContext): Boolean {
        return left.eval(ctx) && right.eval(ctx)
    }
}

@Keep
@Serializable
@SerialName(OpCodes.OR)
class OrExpression(
    @ProtoNumber(1)
    private val left: Expression<Boolean>,
    @ProtoNumber(2)
    private val right: Expression<Boolean>
) : Expression<Boolean> {
    override fun eval(ctx: EvalContext): Boolean {
        return left.eval(ctx) || right.eval(ctx)
    }
}

@Keep
@Serializable
@SerialName(OpCodes.NOT)
class NotExpression(
    @ProtoNumber(1)
    private val expression: Expression<Boolean>
) : Expression<Boolean> {
    override fun eval(ctx: EvalContext): Boolean {
        return !expression.eval(ctx)
    }
}
