@file:OptIn(ExperimentalUuidApi::class)

package app.linksheet.feature.scenario.ui

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import app.linksheet.compose.extension.collectOnIO
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import app.linksheet.feature.engine.database.entity.Scenario
import app.linksheet.feature.scenario.viewmodel.ScenarioViewModel
import fe.composekit.component.ContentType
import fe.composekit.component.PreviewThemeNew
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun ScenarioRoute(
    id: Uuid,
    onBackPressed: () -> Unit,
    viewModel: ScenarioViewModel = koinViewModel(
        parameters = { parametersOf(id) }
    ),
) {
    val scenario by viewModel.getScenario().collectOnIO(null)
    scenario?.let {
        ScenarioRouteInternal(
            scenario = it,
            onBackPressed = onBackPressed,
            onSave = { rule ->
                viewModel.save(rule)
            }
        )
    }
}

@Composable
private fun ScenarioRouteInternal(
    scenario: Scenario,
    onBackPressed: () -> Unit,
    onSave: (String) -> Unit
) {
    val lazyListState = rememberLazyListState()
    SaneScaffoldSettingsPage(
        headline = scenario.name,
//        headline = stringResource(id = R.string.settings_scenarios__title_scenarios),
        onBackPressed = onBackPressed,
        state = lazyListState
    ) {
        item(key = 1, contentType = ContentType.SingleGroupItem) {
            val state = rememberTextFieldState()
            TextField(state = state)

            Button(onClick = {
               onSave(state.text.toString())
            }) {
                Text(text = "Save")
            }
        }
    }
}


@Preview
@Composable
private fun ScenarioRoutePreview() {
    PreviewThemeNew {
        ScenarioRouteInternal(
            scenario = Scenario(
                id = Uuid.fromLongs(0, 2 shl 64),
                name = "Test scenario 2",
                position = 1,
                referrerApp = null
            ),
            onBackPressed = {},
            onSave = {}
        )
    }
}
