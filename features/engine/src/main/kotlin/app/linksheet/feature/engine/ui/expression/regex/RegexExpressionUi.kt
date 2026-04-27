package app.linksheet.feature.engine.ui.expression.regex

import androidx.compose.runtime.Composable
import app.linksheet.feature.engine.eval.expression.RegexExpression
import app.linksheet.feature.engine.ui.expression.RootExpressionUi

@Composable
internal fun RegexExpressionUi(expression: RegexExpression) {
    RootExpressionUi(expression = expression.expression)
}
