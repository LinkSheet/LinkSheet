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
@SerialName(OpCodes.EQ)
class EqualsExpression<T : Comparable<T>>(
    @ProtoNumber(1)
    private val left: Expression<T>,
    @ProtoNumber(2)
    private val right: Expression<T>,
) : Expression<Boolean> {

    override fun eval(ctx: EvalContext): Boolean {
        return compareTo(left, right, ctx) == 0
    }
}

@Keep
@Serializable
@SerialName(OpCodes.LT)
class LessThanExpression<T : Comparable<T>>(
    @ProtoNumber(1)
    private val left: Expression<T>,
    @ProtoNumber(2)
    private val right: Expression<T>,
) : Expression<Boolean> {

    override fun eval(ctx: EvalContext): Boolean {
        return compareTo(left, right, ctx) < 0
    }
}

@Keep
@Serializable
@SerialName(OpCodes.LTE)
class LessThanEqualExpression<T : Comparable<T>>(
    @ProtoNumber(1)
    private val left: Expression<T>,
    @ProtoNumber(2)
    private val right: Expression<T>,
) : Expression<Boolean> {

    override fun eval(ctx: EvalContext): Boolean {
        return compareTo(left, right, ctx) <= 0
    }
}

@Keep
@Serializable
@SerialName(OpCodes.GT)
class GreaterThanExpression<T : Comparable<T>>(
    @ProtoNumber(1)
    private val left: Expression<T>,
    @ProtoNumber(2)
    private val right: Expression<T>,
) : Expression<Boolean> {

    override fun eval(ctx: EvalContext): Boolean {
        return compareTo(left, right, ctx) > 0
    }
}

@Keep
@Serializable
@SerialName(OpCodes.GTE)
class GreaterThanEqualExpression<T : Comparable<T>>(
    @ProtoNumber(1)
    private val left: Expression<T>,
    @ProtoNumber(2)
    private val right: Expression<T>,
) : Expression<Boolean> {

    override fun eval(ctx: EvalContext): Boolean {
        return compareTo(left, right, ctx) >= 0
    }
}

private fun <T : Comparable<T>> compareTo(left: Expression<T>, right: Expression<T>, ctx: EvalContext): Int {
    return left.eval(ctx).compareTo(right.eval(ctx))
}
