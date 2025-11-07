package app.linksheet.feature.engine.eval.expression

import app.linksheet.feature.engine.eval.EvalContext
import kotlinx.serialization.Serializable

@Serializable
sealed interface Expression<out T> {
    fun eval(ctx: EvalContext): T
}

sealed interface LeftRightExpression<T> : Expression<Boolean> {
    val left: Expression<T>
    val right: Expression<T>
}
