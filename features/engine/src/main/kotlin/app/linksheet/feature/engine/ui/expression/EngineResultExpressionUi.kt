package app.linksheet.feature.engine.ui.expression

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.linksheet.feature.engine.eval.expression.EngineResultExpression
import app.linksheet.feature.engine.eval.expression.ForwardOtherProfileResultExpression
import app.linksheet.feature.engine.eval.expression.IntentEngineResultExpression
import app.linksheet.feature.engine.eval.expression.UrlEngineResultExpression

@Composable
fun EngineResultExpressionUi(expression: EngineResultExpression) {
    when (expression) {
        is UrlEngineResultExpression -> Placeholder(expression::class.simpleName)
        is ForwardOtherProfileResultExpression -> Placeholder(expression::class.simpleName)
        is IntentEngineResultExpression -> IntentEngineResultExpressionUi(expression = expression)
    }
}

@Composable
private fun IntentEngineResultExpressionUi(expression: IntentEngineResultExpression) {
    Column {
        Text(text = "return")
        RootExpressionUi(expression = expression.expression)
    }
}
