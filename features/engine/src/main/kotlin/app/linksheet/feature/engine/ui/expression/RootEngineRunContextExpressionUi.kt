package app.linksheet.feature.engine.ui.expression

import androidx.compose.runtime.Composable
import app.linksheet.feature.engine.eval.expression.AddFlagExpression
import app.linksheet.feature.engine.eval.expression.EngineRunContextExpression
import app.linksheet.feature.engine.eval.expression.GetSourceAppExtraExpression
import app.linksheet.feature.engine.eval.expression.HasExtraExpression
import app.linksheet.feature.engine.eval.expression.PutAppRoleExpression

@Composable
fun RootEngineRunContextExpressionUi(expression: EngineRunContextExpression) {
    when(expression) {
        is AddFlagExpression -> Placeholder(expression::class.simpleName)
        is GetSourceAppExtraExpression -> Placeholder(expression::class.simpleName)
        is HasExtraExpression -> Placeholder(expression::class.simpleName)
        is PutAppRoleExpression -> Placeholder(expression::class.simpleName)
    }
}
