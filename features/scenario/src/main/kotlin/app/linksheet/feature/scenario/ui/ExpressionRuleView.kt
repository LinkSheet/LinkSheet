package app.linksheet.feature.scenario.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import app.linksheet.feature.engine.eval.BundleSerializer
import app.linksheet.feature.engine.eval.ExpressionStringifier
import fe.composekit.component.PreviewThemeNew


@Composable
fun ExpressionRuleView(rule: String, pseudoCode: String) {
    Text(
        text = pseudoCode,
        fontFamily = FontFamily.Monospace
    )
}

@Preview(showBackground = true)
@Composable
private fun ExpressionRuleViewPreview() {
    val expr = "080112b0010a02696612a9010a3f0a055f722e6d6512360a210a025f72121b0a190a016312140a1268747470733a2f2f745c2e6d652f282e2b2912110a027573120b0a090a012412040a02727512660a023d6912600a5e0a04702d3e6912560a210a0163121c0a1a616e64726f69642e696e74656e742e616374696f6e2e5649455712120a035f6175120b0a090a012412040a0272751a1d0a016312180a166f72672e74656c656772616d2e6d657373656e676572"
    val bundle = BundleSerializer.decodeFromHexString(expr)

    val str = ExpressionStringifier().stringify(bundle)
    PreviewThemeNew {
        ExpressionRuleView(expr, str)
    }
}
