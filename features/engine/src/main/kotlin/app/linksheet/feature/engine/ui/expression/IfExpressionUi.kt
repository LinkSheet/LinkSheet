package app.linksheet.feature.engine.ui.expression

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Rule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import app.linksheet.compose.preview.PreviewTheme
import app.linksheet.feature.engine.core.context.EngineFlag
import app.linksheet.feature.engine.eval.KnownTokens
import app.linksheet.feature.engine.eval.expression.AddFlagExpression
import app.linksheet.feature.engine.eval.expression.ConstantExpression
import app.linksheet.feature.engine.eval.expression.IfExpression
import app.linksheet.feature.engine.ui.ExpressionInputChip

@Composable
fun <T> IfExpressionUi(expression: IfExpression<T>) {
    Card(
        modifier = Modifier
            .clip(InputChipDefaults.shape),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
    ) {
//        Box(
//            modifier = Modifier
//                .padding(all = 8.dp)
//                .dashedBorder(
//                    text = "Intent",
//                    color = MaterialTheme.colorScheme.primary,
//                    backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
//                    shape = InputChipDefaults.shape
//                )
//        ) {
        Column(modifier = Modifier.padding(all = 8.dp)) {
            LazyColumn(
//                contentPadding = PaddingValues(horizontal = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                item {
                    ExpressionInputChip(
                        label = {
                            RootExpressionUi(expression = expression.condition)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Rule,
                                contentDescription = null
                            )
                        }
                    )
                }
                item {
                    ExpressionInputChip(
                        label = {
                            RootExpressionUi(expression = expression.body)
                        },
                        leadingIcon = {

                        }
                    )
                }
            }
        }
    }
}

private class IfExpressionUiPreviewProvider : PreviewParameterProvider<IfExpression<*>> {
    override val values: Sequence<IfExpression<*>> = sequenceOf(
        IfExpression(
            condition = ConstantExpression(true),
            body = AddFlagExpression(
                expression = KnownTokens.EngineRunContext,
                flag = ConstantExpression(EngineFlag.DisablePreview)
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun IfExpressionUiPreview(
    @PreviewParameter(IfExpressionUiPreviewProvider::class) expression: IfExpression<*>
) {
    PreviewTheme {
        IfExpressionUi(expression = expression)
    }
}
