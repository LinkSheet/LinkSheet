package app.linksheet.feature.engine.ui

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.linksheet.compose.preview.PreviewTheme
import app.linksheet.feature.engine.eval.BundleSerializer
import app.linksheet.feature.engine.eval.KnownTokens
import app.linksheet.feature.engine.eval.expression.AddFlagExpression
import app.linksheet.feature.engine.eval.expression.AndExpression
import app.linksheet.feature.engine.eval.expression.BlockExpression
import app.linksheet.feature.engine.eval.expression.ComponentNameExpression
import app.linksheet.feature.engine.eval.expression.ConstantExpression
import app.linksheet.feature.engine.eval.expression.EqualsExpression
import app.linksheet.feature.engine.eval.expression.Expression
import app.linksheet.feature.engine.eval.expression.ForwardOtherProfileResultExpression
import app.linksheet.feature.engine.eval.expression.GetSourceAppExtraExpression
import app.linksheet.feature.engine.eval.expression.GreaterThanEqualExpression
import app.linksheet.feature.engine.eval.expression.GreaterThanExpression
import app.linksheet.feature.engine.eval.expression.HasExtraExpression
import app.linksheet.feature.engine.eval.expression.IfExpression
import app.linksheet.feature.engine.eval.expression.InjectTokenExpression
import app.linksheet.feature.engine.eval.expression.IntentComponentNameExpression
import app.linksheet.feature.engine.eval.expression.IntentEngineResultExpression
import app.linksheet.feature.engine.eval.expression.IntentPackageExpression
import app.linksheet.feature.engine.eval.expression.LeftRightExpression
import app.linksheet.feature.engine.eval.expression.LessThanEqualExpression
import app.linksheet.feature.engine.eval.expression.LessThanExpression
import app.linksheet.feature.engine.eval.expression.NotEqualsExpression
import app.linksheet.feature.engine.eval.expression.NotExpression
import app.linksheet.feature.engine.eval.expression.OrExpression
import app.linksheet.feature.engine.eval.expression.PutAppRoleExpression
import app.linksheet.feature.engine.eval.expression.RegexExpression
import app.linksheet.feature.engine.eval.expression.RegexMatchEntireExpression
import app.linksheet.feature.engine.eval.expression.StringContainsExpression
import app.linksheet.feature.engine.eval.expression.StringEqualsExpression
import app.linksheet.feature.engine.eval.expression.UrlEngineResultExpression
import app.linksheet.feature.engine.eval.expression.UrlGetComponentExpression
import app.linksheet.feature.engine.eval.expression.UrlQueryParamExpression
import app.linksheet.feature.engine.eval.expression.UrlSetComponentExpression
import app.linksheet.feature.engine.eval.expression.UrlStringExpression
import app.linksheet.feature.engine.eval.expression.UrlToAndroidUriExpression
import fe.android.compose.extension.optionalClickable
import fe.composekit.component.PreviewThemeNew
import fe.composekit.component.card.AlertCardDefaults
import fe.composekit.component.shape.CustomShapeDefaults
import kotlin.math.exp


@Composable
fun <T> RootExpressionUi(modifier: Modifier = Modifier, expression: Expression<T>) {
    Box(modifier = modifier) {
        when (expression) {
            is AddFlagExpression -> Placeholder()
            is BlockExpression -> Placeholder()
            is ComponentNameExpression -> ComponentNameExpressionUi(expression = expression)
            is ConstantExpression<*> -> ConstantExpressionUi(expression = expression)
            is ForwardOtherProfileResultExpression -> Placeholder()
            is GetSourceAppExtraExpression -> Placeholder()
            is HasExtraExpression -> Placeholder()
            is IfExpression<*> -> IfExpressionUi(expression = expression)
            is InjectTokenExpression<*> -> InjectTokenExpressionUi(expression = expression)
            is IntentComponentNameExpression -> {
                IntentComponentNameExpressionUi(expression = expression)
            }

            is IntentEngineResultExpression -> {
                IntentEngineResultExpressionUi(expression = expression)
            }

            is IntentPackageExpression -> {
                IntentPackageExpressionUi(expression = expression)
            }

            is NotExpression -> Placeholder()
            is PutAppRoleExpression -> Placeholder()
            is RegexExpression -> RegexExpressionUi(expression = expression)
            is RegexMatchEntireExpression -> RegexMatchEntireExpressionUi(expression = expression)
            is StringContainsExpression -> Placeholder()
            is StringEqualsExpression -> Placeholder()
            is UrlEngineResultExpression -> Placeholder()
            is UrlGetComponentExpression -> Placeholder()
            is UrlQueryParamExpression -> Placeholder()
            is UrlSetComponentExpression -> Placeholder()
            is UrlStringExpression -> UrlStringExpressionUi(expression = expression)
            is UrlToAndroidUriExpression -> UrlToAndroidUriExpressionUi(expression = expression)
            is LeftRightExpression<*> -> {
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
                        LeftRightExpressionUi(expression = expression, value = "||")
                    }
                    is AndExpression -> {
                        LeftRightExpressionUi(expression = expression, value = "&&")
                    }
                }
            }
        }
    }
}

@Composable
fun UrlToAndroidUriExpressionUi(expression: UrlToAndroidUriExpression) {
    RootExpressionUi(expression = expression.expression)
}

@Composable
fun IntentEngineResultExpressionUi(expression: IntentEngineResultExpression) {
    Column {
        Text(text = "return")
        RootExpressionUi(expression = expression.expression)
    }
}

@Composable
fun <T> LeftRightExpressionUi(expression: LeftRightExpression<T>, value: String) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        item {
            InputChip(
                selected = true,
                label = {
                    RootExpressionUi(expression = expression.left)
                },
                onClick = {

                }
            )
        }
        item {
            AssistChip(label = { Text(text = value) }, onClick = {})
        }
        item {
            InputChip(
                selected = true,
                label = {
                    RootExpressionUi(expression = expression.right)
                },
                onClick = {

                }
            )
        }
    }
}

@Composable
fun UrlStringExpressionUi(expression: UrlStringExpression) {
    RootExpressionUi(expression = expression.expression)
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
fun RegexMatchEntireExpressionUi(expression: RegexMatchEntireExpression) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(InputChipDefaults.shape),
//            .optionalClickable(onClick = {}),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
            ) {
                Column(modifier = Modifier.padding(all = 10.dp)) {
                    Text(text = "Regex", textAlign = TextAlign.Start)
                    ExpressionInputChip(label = { RootExpressionUi(expression = expression.regex) })
                }
            }
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
fun RegexExpressionUi(expression: RegexExpression) {
    RootExpressionUi(expression = expression.expression)
}

@Composable
private fun <T> IfExpressionUi(expression: IfExpression<T>) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(CustomShapeDefaults.SingleShape)
////            .optionalClickable(onClick = {
////
////            })
//            ,
//        colors = CardDefaults.cardColors()
//    ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AlertCardDefaults.InnerPadding),
//            horizontalArrangement = AlertCardDefaults.HorizontalArrangement,
//            verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Condition")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            RootExpressionUi(
                modifier = Modifier.weight(0.9f),
                expression = expression.condition
            )
            FilledTonalIconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null
                )
            }
        }
        Text(text = "Action")
        RootExpressionUi(expression = expression.body)
    }
//    }
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
private fun Placeholder() {

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

@Preview(showBackground = true)
@Composable
private fun RootExpressionUiPreview2() {
    val expression = IfExpression(
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
    )

    PreviewTheme {
        RootExpressionUi(expression = expression)
    }
}

fun Modifier.dashedBorder(
    color: Color,
    shape: Shape,
    strokeWidth: Dp = 2.dp,
    dashWidth: Dp = 4.dp,
    gapWidth: Dp = 4.dp,
    cap: StrokeCap = StrokeCap.Round,
) = drawWithContent {
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
        color = color
    )
}


