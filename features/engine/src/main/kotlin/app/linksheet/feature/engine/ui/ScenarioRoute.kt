@file:OptIn(ExperimentalUuidApi::class)

package app.linksheet.feature.engine.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import app.linksheet.compose.extension.collectOnIO
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import app.linksheet.feature.engine.R
import app.linksheet.feature.engine.database.entity.ExpressionRule
import app.linksheet.feature.engine.database.entity.ExpressionRuleType
import app.linksheet.feature.engine.database.entity.Scenario
import app.linksheet.feature.engine.eval.BundleSerializer
import app.linksheet.feature.engine.eval.ExpressionStringifier
import app.linksheet.feature.engine.viewmodel.ScenarioViewModel
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.DefaultContent.Companion.text
import fe.composekit.component.PreviewThemeNew
import fe.composekit.component.list.column.shape.ClickableShapeListItem
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.layout.column.group
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun ScenarioRoute(
    id: Long,
    onBackPressed: () -> Unit,
    viewModel: ScenarioViewModel = koinViewModel(
        parameters = { parametersOf(id) }
    ),
) {
//    val scenario by viewModel.getScenario().collectOnIO(null)
    val scenario by viewModel.getScenarioExpressions().collectOnIO(null)
    scenario?.let { (scenario, rules) ->
        ScenarioRouteInternal(
            scenario = scenario,
            rules = rules,
            onBackPressed = onBackPressed,
            toString = viewModel::toString,
            onSave = { rule ->
                viewModel.save(rule)
            }
        )
    }
}

@Composable
private fun ScenarioRouteInternal(
    scenario: Scenario,
    rules: List<ExpressionRule>,
    toString: (ExpressionRule) -> String,
    onBackPressed: () -> Unit,
    onSave: (String) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    SaneScaffoldSettingsPage(
        headline = scenario.name,
//        headline = stringResource(id = R.string.settings_scenarios__title_scenarios),
        onBackPressed = onBackPressed,
        state = lazyListState
    ) {
        divider(id = R.string.settings_scenario__divider_rules)

        group(list = rules, key = { it.id }) { data, padding, shape ->
            ExpressionRuleListItem(data = data, padding = padding, shape = shape, toString = toString)
        }

//        item(key = 1, contentType = ContentType.SingleGroupItem) {
//            val state = rememberTextFieldState()
//            TextField(state = state)
//
//            Button(onClick = {
//               onSave(state.text.toString())
//            }) {
//                Text(text = "Save")
//            }
//        }
    }
}

@Composable
private fun ExpressionRuleListItem(
    data: ExpressionRule,
    padding: PaddingValues,
    shape: Shape,
    toString: (ExpressionRule) -> String,
) {
    val code = remember(data) { toString(data) }
    ClickableShapeListItem(
        padding = padding,
        shape = shape,
        position = ContentPosition.Leading,
        headlineContent = text(data.id.toString()),
        supportingContent = content {
            Text(
                text = code,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        },
        role = Role.Button,
        onClick = {

        },
    )
}


@Composable
private fun ExpressionRuleListItemPreview(
    data: ExpressionRule,
) {

}


@Preview
@Composable
private fun ScenarioRoutePreview() {
    PreviewThemeNew {
        ScenarioRouteInternal(
            scenario = Scenario(
                name = "Default scenario",
                position = 1,
                referrerApp = null
            ),
            rules = listOf(
                ExpressionRule(
                    id = 1,
                    bytes = "080112b0010a02696612a9010a3f0a055f722e6d6512360a210a025f72121b0a190a016312140a1268747470733a2f2f745c2e6d652f282e2b2912110a027573120b0a090a012412040a02727512660a023d6912600a5e0a04702d3e6912560a210a0163121c0a1a616e64726f69642e696e74656e742e616374696f6e2e5649455712120a035f6175120b0a090a012412040a0272751a1d0a016312180a166f72672e74656c656772616d2e6d657373656e676572".hexToByteArray(),
                    type = ExpressionRuleType.Post
                )
            ),
            toString = {
                ExpressionStringifier.stringify(BundleSerializer.decodeFromByteArray(it.bytes))
            },
            onBackPressed = {},
            onSave = {},
        )
    }
}
