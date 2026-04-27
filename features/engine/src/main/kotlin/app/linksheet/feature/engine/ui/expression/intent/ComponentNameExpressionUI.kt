package app.linksheet.feature.engine.ui.expression.intent

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import app.linksheet.compose.preview.PreviewTheme
import app.linksheet.feature.engine.eval.expression.ComponentNameExpression
import app.linksheet.feature.engine.eval.expression.ConstantExpression
import app.linksheet.feature.engine.ui.expression.RootExpressionUi

@Composable
internal fun ComponentNameExpressionUi(expression: ComponentNameExpression) {
    FlowRow() {
        RootExpressionUi(expression = expression.pkg)
        Text(text = " / ")
        RootExpressionUi(expression = expression.cls)
    }
}


private class ComponentNameExpressionUiPreviewProvider : PreviewParameterProvider<ComponentNameExpression> {
    override val values: Sequence<ComponentNameExpression> = sequenceOf(
        ComponentNameExpression(
            pkg = ConstantExpression("com.dv.adm"),
            cls = ConstantExpression("com.dv.adm.AEditor")
        ),
        ComponentNameExpression(
            pkg = ConstantExpression("this.is.a.very.long.package.name"),
            cls = ConstantExpression("this.is.a.very.long.package.name.some.package.activity.SomeActivity")
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ComponentNameExpressionUiPreview(
    @PreviewParameter(ComponentNameExpressionUiPreviewProvider::class) expression: ComponentNameExpression
) {
    PreviewTheme {
        ComponentNameExpressionUi(expression = expression)
    }
}
