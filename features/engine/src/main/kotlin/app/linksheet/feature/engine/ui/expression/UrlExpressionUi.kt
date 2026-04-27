package app.linksheet.feature.engine.ui.expression

import androidx.compose.runtime.Composable
import app.linksheet.feature.engine.eval.expression.UrlGetComponentExpression
import app.linksheet.feature.engine.eval.expression.UrlOperationExpression
import app.linksheet.feature.engine.eval.expression.UrlQueryParamExpression
import app.linksheet.feature.engine.eval.expression.UrlSetComponentExpression
import app.linksheet.feature.engine.eval.expression.UrlStringExpression
import app.linksheet.feature.engine.eval.expression.UrlToAndroidUriExpression

@Composable
fun RootUrlExpressionUi(expression: UrlOperationExpression) {
    when (expression) {
        is UrlGetComponentExpression -> Placeholder()
        is UrlQueryParamExpression -> Placeholder()
        is UrlSetComponentExpression -> Placeholder()
        is UrlStringExpression -> UrlStringExpressionUi(expression = expression)
        is UrlToAndroidUriExpression -> UrlToAndroidUriExpressionUi(expression = expression)
    }
}

@Composable
fun UrlToAndroidUriExpressionUi(expression: UrlToAndroidUriExpression) {
    RootExpressionUi(expression = expression.expression)
}

@Composable
fun UrlStringExpressionUi(expression: UrlStringExpression) {
    RootExpressionUi(expression = expression.expression)
}

