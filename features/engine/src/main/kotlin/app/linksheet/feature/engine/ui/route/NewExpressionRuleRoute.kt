package app.linksheet.feature.engine.ui.route

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import app.linksheet.compose.extension.collectOnIO
import app.linksheet.compose.page.SaneScaffoldSettingsPage
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
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import app.linksheet.compose.R as CommonR

@Composable
fun NewExpressionRule(
    id: Long,
    onBackPressed: () -> Unit,
    viewModel: ScenarioViewModel = koinViewModel(
        parameters = { parametersOf(id) }
    ),
) {
    val scenario by viewModel.getScenario().collectOnIO(null)
//    val scenarioInfo by viewModel.getScenarioExpressions().collectOnIO(null)
    scenario?.let {
        NewExpressionRuleInternal(
            scenario = it,
            rules = emptyList(),
            onBackPressed = onBackPressed,
            toString = viewModel::toString,
            onSave = { rule ->
                viewModel.save(rule)
            }
        )
    }
}

@Composable
private fun NewExpressionRuleInternal(
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
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                onClick = {

                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Save,
                    contentDescription = stringResource(id = CommonR.string.generic__button_text_save)
                )
            }
        },
        state = lazyListState
    ) {
        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                state = rememberTextFieldState(),
                lineLimits = TextFieldLineLimits.SingleLine,
                label = { Text("Label") },
            )
        }

        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                state = rememberTextFieldState(),
                lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 5),
                label = { Text("Label") },
                labelPosition = TextFieldLabelPosition.Above(),
            )
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
private fun NewExpressionRulePreview() {
    PreviewThemeNew {
        NewExpressionRuleInternal(
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
