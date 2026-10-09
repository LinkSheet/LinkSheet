package app.linksheet.feature.engine.ui.expression.intent

import androidx.compose.runtime.Composable
import app.linksheet.feature.engine.eval.expression.ComponentNameExpression
import app.linksheet.feature.engine.eval.expression.IntentComponentNameExpression
import app.linksheet.feature.engine.eval.expression.IntentExpression
import app.linksheet.feature.engine.eval.expression.IntentPackageExpression

@Composable
fun RootIntentExpressionUi(expression: IntentExpression) {
    when (expression) {
        is ComponentNameExpression -> ComponentNameExpressionUi(expression = expression)
        is IntentComponentNameExpression -> IntentComponentNameExpressionUi(expression = expression)
        is IntentPackageExpression -> IntentPackageExpressionUi(expression = expression)
    }
}
