package app.linksheet.feature.engine.ui.expression.regex

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import app.linksheet.compose.preview.PreviewTheme
import app.linksheet.feature.engine.R
import app.linksheet.feature.engine.eval.KnownTokens
import app.linksheet.feature.engine.eval.expression.ConstantExpression
import app.linksheet.feature.engine.eval.expression.RegexExpression
import app.linksheet.feature.engine.eval.expression.RegexMatchEntireExpression
import app.linksheet.feature.engine.eval.expression.UrlStringExpression
import app.linksheet.feature.engine.ui.ExpressionAssistChip
import app.linksheet.feature.engine.ui.ExpressionInputChip
import app.linksheet.feature.engine.ui.expression.RootExpressionUi

@Composable
fun RegexMatchEntireExpressionUi(expression: RegexMatchEntireExpression) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            ExpressionInputChip(
                label = { RootExpressionUi(expression = expression.regex) },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.rounded_regular_expression_24),
                        contentDescription = null
                    )
                }
            )
        }
        item {
            ExpressionAssistChip(text = "matchesEntire")
        }
        item {
            ExpressionInputChip(label = { RootExpressionUi(expression = expression.string) })
        }
    }
}


@Composable
fun RegexMatchEntireExpressionUi2(expression: RegexMatchEntireExpression) {

    Card(
        modifier = Modifier
            .clip(InputChipDefaults.shape),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
    ) {
        Column(modifier = Modifier.padding(all = 8.dp)) {
            LazyColumn(
//                contentPadding = PaddingValues(horizontal = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                item {
                    ExpressionInputChip(
                        label = { RootExpressionUi(expression = expression.regex) },
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.rounded_regular_expression_24),
                                contentDescription = null
                            )
                        }
                    )
                }
                item {
                    ExpressionInputChip(
                        label = { RootExpressionUi(expression = expression.string) },
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.rounded_equal_24),
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
//        }
    }
}

private class RegexMatchEntireExpressionPreviewProvider() : PreviewParameterProvider<String> {
    override val values: Sequence<String> = sequenceOf(
        """https?://.*\.(mp3|wav|flac|m4a|aac|oog|mp4)(/.*|\?.*)?$""",
        "https?://t.me/(.+)"
    )
}

@Preview(showBackground = true)
@Composable
private fun RegexMatchEntireExpressionUiPreview(
    @PreviewParameter(RegexMatchEntireExpressionPreviewProvider::class) regex: String
) {
    PreviewTheme {
        RegexMatchEntireExpressionUi2(
            expression = RegexMatchEntireExpression(
                regex = RegexExpression(
                    expression = ConstantExpression(regex)
                ),
                string = UrlStringExpression(
                    expression = KnownTokens.ResultUrl,
                )
            )
        )
    }
}
