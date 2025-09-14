@file:OptIn(ExperimentalSerializationApi::class)

package fe.linksheet.eval.expression

import androidx.annotation.Keep
import fe.linksheet.eval.EvalContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Keep
@Serializable
@SerialName(OpCodes.CONST)
class ConstantExpression<T>(
    @ProtoNumber(1)
    val const: T
) : Expression<T> {
    override fun eval(ctx: EvalContext): T = const
}
