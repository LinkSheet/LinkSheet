package fe.linksheet.eval.expression

import fe.linksheet.eval.EvalContext
import kotlinx.serialization.Serializable

@Serializable
sealed interface Expression<out T> {
    fun eval(ctx: EvalContext): T
}
