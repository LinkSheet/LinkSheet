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
@SerialName(OpCodes.IF)
class IfExpression<T>(
    @ProtoNumber(1)
    val condition: Expression<Boolean>,
    @ProtoNumber(2)
    val body: Expression<T>
) : Expression<T?> {
    override fun eval(ctx: EvalContext): T? {
        val result = condition.eval(ctx)
        if (!result) return null
        return body.eval(ctx)
    }
}
