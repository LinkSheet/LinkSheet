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
@SerialName(OpCodes.INJECT_TOKEN)
class InjectTokenExpression<T>(
    @ProtoNumber(1)
    val name: String
) : Expression<T> {
    override fun eval(ctx: EvalContext): T = ctx.get(name)
}

fun <T> InjectTokenExpression<T>.toInput(input: T): Pair<String, T> {
    return name to input
}
