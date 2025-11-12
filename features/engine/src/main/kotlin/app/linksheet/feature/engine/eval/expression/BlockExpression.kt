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
@SerialName(OpCodes.BLOCK)
class BlockExpression(
    @ProtoNumber(1)
    val expressions: List<Expression<*>> = emptyList(),
) : Expression<Unit> {
    override fun eval(ctx: EvalContext) {
        for (expression in expressions) {
            expression.eval(ctx)
        }
    }
}
