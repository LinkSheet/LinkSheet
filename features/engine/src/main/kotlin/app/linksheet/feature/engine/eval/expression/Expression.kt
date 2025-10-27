package app.linksheet.feature.engine.eval.expression

import app.linksheet.feature.engine.eval.EvalContext
import kotlinx.serialization.Serializable

@Serializable
sealed interface Expression<out T> {
    fun eval(ctx: EvalContext): T
}
