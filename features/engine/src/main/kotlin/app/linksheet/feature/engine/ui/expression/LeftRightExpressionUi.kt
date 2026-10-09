package app.linksheet.feature.engine.ui.expression

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import app.linksheet.compose.preview.PreviewTheme
import app.linksheet.feature.engine.eval.expression.AndExpression
import app.linksheet.feature.engine.eval.expression.ConstantExpression
import app.linksheet.feature.engine.eval.expression.EqualsExpression
import app.linksheet.feature.engine.eval.expression.GreaterThanEqualExpression
import app.linksheet.feature.engine.eval.expression.GreaterThanExpression
import app.linksheet.feature.engine.eval.expression.LeftRightExpression
import app.linksheet.feature.engine.eval.expression.LessThanEqualExpression
import app.linksheet.feature.engine.eval.expression.LessThanExpression
import app.linksheet.feature.engine.eval.expression.NotEqualsExpression
import app.linksheet.feature.engine.eval.expression.OrExpression
import app.linksheet.feature.engine.ui.ExpressionInputChip

@Composable
fun <T> RootLeftRightExpressionUi(expression: LeftRightExpression<T>) {
    when (expression) {
        is EqualsExpression<*> -> {
            LeftRightExpressionUi(expression = expression, value = "==")
        }

        is NotEqualsExpression<*> -> {
            LeftRightExpressionUi(expression = expression, value = "!=")
        }

        is GreaterThanEqualExpression<*> -> {
            LeftRightExpressionUi(expression = expression, value = ">=")
        }

        is GreaterThanExpression<*> -> {
            LeftRightExpressionUi(expression = expression, value = ">")
        }

        is LessThanEqualExpression<*> -> {
            LeftRightExpressionUi(expression = expression, value = "<=")
        }

        is LessThanExpression<*> -> {
            LeftRightExpressionUi(expression = expression, value = "<")
        }

        is OrExpression -> {
            LeftRightExpressionUi(expression = expression, value = "or")
        }

        is AndExpression -> {
            LeftRightExpressionUi(expression = expression, value = "and")
        }
    }
}

@Composable
private fun <T> LeftRightExpressionUi(expression: LeftRightExpression<T>, value: String) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        item {
            ExpressionInputChip(
                label = {
                    RootExpressionUi(expression = expression.left)
                }
            )
        }
        item {
            ExpressionInputChip(
                label = {
                    Text(text = value)
//                    Icon(
//                        modifier = Modifier.size(24.dp),
//                        imageVector = ImageVector.vectorResource(R.drawable.rounded_equal_24),
//                        contentDescription = null
//                    )
                }
            )
//            AssistChip(label = { Text(text = value) }, onClick = {})
        }
        item {
            ExpressionInputChip(
                label = {
                    RootExpressionUi(expression = expression.right)
                }
            )
        }
    }
}

private class LeftRightExpressionUiPreviewProvider : PreviewParameterProvider<LeftRightExpression<*>> {
    override val values: Sequence<LeftRightExpression<*>> = sequenceOf(
        EqualsExpression(
            left = ConstantExpression("left"),
            right = ConstantExpression("right"),
        ),
        NotEqualsExpression(
            left = ConstantExpression("left"),
            right = ConstantExpression("right"),
        ),
        LessThanExpression(
            left = ConstantExpression("left"),
            right = ConstantExpression("right"),
        ),
        LessThanEqualExpression(
            left = ConstantExpression("left"),
            right = ConstantExpression("right"),
        ),
        GreaterThanExpression(
            left = ConstantExpression("left"),
            right = ConstantExpression("right"),
        ),
        GreaterThanEqualExpression(
            left = ConstantExpression("left"),
            right = ConstantExpression("right"),
        ),
        OrExpression(
            left = ConstantExpression(true),
            right = ConstantExpression(false),
        ),
        AndExpression(
            left = ConstantExpression(true),
            right = ConstantExpression(false),
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun LeftRightExpressionUiPreview(
    @PreviewParameter(LeftRightExpressionUiPreviewProvider ::class) expression: LeftRightExpression<*>
) {
    PreviewTheme {
        RootLeftRightExpressionUi(expression = expression)
    }
}
