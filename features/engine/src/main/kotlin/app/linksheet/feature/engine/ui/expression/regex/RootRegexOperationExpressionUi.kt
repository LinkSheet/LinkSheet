package app.linksheet.feature.engine.ui.expression.regex

import androidx.compose.runtime.Composable
import app.linksheet.feature.engine.eval.expression.RegexExpression
import app.linksheet.feature.engine.eval.expression.RegexMatchEntireExpression
import app.linksheet.feature.engine.eval.expression.RegexOperationExpression

@Composable
fun RootRegexOperationExpressionUi(expression: RegexOperationExpression) {
    when (expression) {
        is RegexExpression -> RegexExpressionUi(expression = expression)
        is RegexMatchEntireExpression -> RegexMatchEntireExpressionUi2(expression = expression)
    }
}
