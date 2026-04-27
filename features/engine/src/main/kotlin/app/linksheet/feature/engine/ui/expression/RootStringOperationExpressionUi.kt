package app.linksheet.feature.engine.ui.expression

import androidx.compose.runtime.Composable
import app.linksheet.feature.engine.eval.expression.StringContainsExpression
import app.linksheet.feature.engine.eval.expression.StringEqualsExpression
import app.linksheet.feature.engine.eval.expression.StringOperationExpression

@Composable
fun RootStringOperationExpressionUi(expression: StringOperationExpression) {
    when (expression) {
        is StringContainsExpression -> Placeholder(expression::class.simpleName)
        is StringEqualsExpression -> Placeholder(expression::class.simpleName)
    }
}
