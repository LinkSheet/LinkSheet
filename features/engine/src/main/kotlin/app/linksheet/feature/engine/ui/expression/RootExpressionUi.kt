package app.linksheet.feature.engine.ui.expression

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.linksheet.compose.preview.PreviewTheme
import app.linksheet.feature.engine.eval.BundleSerializer
import app.linksheet.feature.engine.eval.KnownTokens
import app.linksheet.feature.engine.eval.expression.BlockExpression
import app.linksheet.feature.engine.eval.expression.ComponentNameExpression
import app.linksheet.feature.engine.eval.expression.ConstantExpression
import app.linksheet.feature.engine.eval.expression.EngineResultExpression
import app.linksheet.feature.engine.eval.expression.EngineRunContextExpression
import app.linksheet.feature.engine.eval.expression.EqualsExpression
import app.linksheet.feature.engine.eval.expression.Expression
import app.linksheet.feature.engine.eval.expression.ForwardOtherProfileResultExpression
import app.linksheet.feature.engine.eval.expression.IfExpression
import app.linksheet.feature.engine.eval.expression.InjectTokenExpression
import app.linksheet.feature.engine.eval.expression.IntentComponentNameExpression
import app.linksheet.feature.engine.eval.expression.IntentEngineResultExpression
import app.linksheet.feature.engine.eval.expression.IntentExpression
import app.linksheet.feature.engine.eval.expression.LeftRightExpression
import app.linksheet.feature.engine.eval.expression.NotExpression
import app.linksheet.feature.engine.eval.expression.RegexExpression
import app.linksheet.feature.engine.eval.expression.RegexMatchEntireExpression
import app.linksheet.feature.engine.eval.expression.RegexOperationExpression
import app.linksheet.feature.engine.eval.expression.StringOperationExpression
import app.linksheet.feature.engine.eval.expression.UrlOperationExpression
import app.linksheet.feature.engine.eval.expression.UrlStringExpression
import app.linksheet.feature.engine.eval.expression.UrlToAndroidUriExpression
import app.linksheet.feature.engine.ui.Text12Monospace
import app.linksheet.feature.engine.ui.expression.intent.RootIntentExpressionUi
import app.linksheet.feature.engine.ui.expression.regex.RootRegexOperationExpressionUi

@Composable
fun <T> RootExpressionUi(modifier: Modifier = Modifier, expression: Expression<T>) {
    Box(modifier = modifier) {
        when (expression) {
            is BlockExpression -> Placeholder(expression::class.simpleName)
            is ConstantExpression<*> -> ConstantExpressionUi(expression)
            is IfExpression<*> -> IfExpressionUi(expression)
            is InjectTokenExpression<*> -> InjectTokenExpressionUi(expression = expression)
            is NotExpression -> Placeholder(expression::class.simpleName)
            is StringOperationExpression -> RootStringOperationExpressionUi(expression)
            is EngineRunContextExpression -> RootEngineRunContextExpressionUi(expression)
            is RegexOperationExpression -> RootRegexOperationExpressionUi(expression)
            is IntentExpression -> RootIntentExpressionUi(expression)
            is UrlOperationExpression -> RootUrlExpressionUi(expression)
            is LeftRightExpression<*> -> RootLeftRightExpressionUi(expression)
            is EngineResultExpression -> EngineResultExpressionUi(expression)
        }
    }
}

@Composable
fun InjectTokenExpressionUi(expression: InjectTokenExpression<*>) {
    val text = when (expression.name) {
        KnownTokens.ResultUrl.name -> $$"$RESULT_URL"
        KnownTokens.OriginalUrl.name -> $$"$ORIGINAL_URL"
        KnownTokens.EngineRunContext.name -> $$"$CONTEXT"
        else -> "$" + expression.name
    }
    Text12Monospace(text = text)
}

@Composable
fun ConstantExpressionUi(expression: ConstantExpression<*>) {
    Text12Monospace(text = expression.const.toString())
}



@Composable
fun InputChipExample(
    text: String,
    onDismiss: () -> Unit,
) {
    var enabled by remember { mutableStateOf(true) }
    if (!enabled) return

    InputChip(
        onClick = {
            onDismiss()
            enabled = !enabled
        },
        label = { Text(text) },
        selected = enabled,
        avatar = {
            Icon(
                Icons.Filled.Person,
                contentDescription = "Localized description",
                Modifier.size(InputChipDefaults.AvatarSize)
            )
        },
        trailingIcon = {
            Icon(
                Icons.Default.Close,
                contentDescription = "Localized description",
                Modifier.size(InputChipDefaults.AvatarSize)
            )
        },
    )
}

@Composable
fun Placeholder(text: String? = null) {
    if (text != null) {
        Text12Monospace(text = "TODO{$text}")
    }
}

@Preview(showBackground = true)
@Composable
private fun RootExpressionUiPreview() {
    val expr =
        "080112b0010a02696612a9010a3f0a055f722e6d6512360a210a025f72121b0a190a016312140a1268747470733a2f2f745c2e6d652f282e2b2912110a027573120b0a090a012412040a02727512660a023d6912600a5e0a04702d3e6912560a210a0163121c0a1a616e64726f69642e696e74656e742e616374696f6e2e5649455712120a035f6175120b0a090a012412040a0272751a1d0a016312180a166f72672e74656c656772616d2e6d657373656e676572"
    val bundle = BundleSerializer.decodeFromHexString(expr)

    PreviewTheme {
        RootExpressionUi(expression = bundle.expression)
    }
}

private class RootExpressionUiPreviewProvider : PreviewParameterProvider<Expression<*>> {
    override val values: Sequence<Expression<*>> = sequenceOf(
        IfExpression(
            condition = RegexMatchEntireExpression(
                regex = RegexExpression(
                    expression = ConstantExpression("""https?://.*\.(mp3|wav|flac|m4a|aac|oog|mp4)(/.*|\?.*)?$""")
                ),
                string = UrlStringExpression(
                    expression = KnownTokens.ResultUrl,
                )
            ),
            body = IntentEngineResultExpression(
                expression = IntentComponentNameExpression(
                    action = ConstantExpression(Intent.ACTION_VIEW),
                    data = UrlToAndroidUriExpression(
                        expression = KnownTokens.ResultUrl
                    ),
                    componentName = ComponentNameExpression(
                        pkg = ConstantExpression("com.dv.adm"),
                        cls = ConstantExpression("com.dv.adm.AEditor")
                    )
                )
            )
        ),
        IfExpression(
            condition = EqualsExpression(
                left = UrlStringExpression(
                    expression = KnownTokens.ResultUrl,
                ),
                right = ConstantExpression("https://google.com")
            ),
            body = ForwardOtherProfileResultExpression(
                expression = KnownTokens.ResultUrl
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun RootExpressionUiPreview2(
    @PreviewParameter(RootExpressionUiPreviewProvider::class) expression: Expression<*>
) {
    PreviewTheme {
        RootExpressionUi(expression = expression)
    }
}

@Composable
fun Modifier.dashedBorder(
    text: String,
    color: Color,
    backgroundColor: Color,
    shape: Shape,
    strokeWidth: Dp = 2.dp,
    dashWidth: Dp = 4.dp,
    gapWidth: Dp = 4.dp,
    cap: StrokeCap = StrokeCap.Round,
): Modifier {
    val textMeasurer = rememberTextMeasurer()
    return drawWithContent {
        val outline = shape.createOutline(size, layoutDirection, this)

        val path = Path()
        path.addOutline(outline)

        val stroke = Stroke(
            cap = cap,
            width = strokeWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(dashWidth.toPx(), gapWidth.toPx()),
                phase = 0f
            )
        )

        this.drawContent()

        drawPath(
            path = path,
            style = stroke,
            color = color,
        )
        val textLayoutResult = textMeasurer.measure(
            text = text,
            style = TextStyle(
                background = backgroundColor
            ),
//                constraints =
//                    Constraints.fixed(
//                        width = (size.width / 2).roundToInt(),
//                        height = (size.height / 2).roundToInt(),
//                    ),
            overflow = TextOverflow.Ellipsis,
        )
        drawText(
            textLayoutResult = textLayoutResult,
            color = color,
            drawStyle = Fill,
            topLeft = Offset(
                16f,
                -(textLayoutResult.size.height / 2).toFloat()
            ),
        )
//        drawText(
//            textLayoutResult = textLayoutResult,
//            color = color,
//            drawStyle = Fill,
//            topLeft = Offset(
//                16f,
//                -(textLayoutResult.size.height / 2).toFloat()
//            ),
//        )

    }
}


