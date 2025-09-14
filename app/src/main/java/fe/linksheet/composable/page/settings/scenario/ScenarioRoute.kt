package fe.linksheet.composable.page.settings.scenario

import androidx.compose.runtime.Composable
import fe.composekit.route.Route
import app.linksheet.feature.scenario.ScenarioViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun ScenarioRoute(
    navigate: (Route) -> Unit,
    viewModel: ScenarioViewModel = koinViewModel(),
) {

}

