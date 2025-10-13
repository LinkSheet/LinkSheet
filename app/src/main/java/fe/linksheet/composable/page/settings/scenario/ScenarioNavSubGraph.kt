package fe.linksheet.composable.page.settings.scenario

import androidx.annotation.Keep
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import fe.composekit.route.Route
import app.linksheet.compose.util.animatedComposable
import app.linksheet.compose.navigation.NavSubGraph
import kotlinx.serialization.Serializable

@Serializable
object ScenarioNavSubGraph : NavSubGraph {
    override val startDestination: Any = ScenarioRoute
    override val graph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        animatedComposable<ScenarioRoute> { _, route ->
        }
    }
}

@Keep
@Serializable
object ScenarioRoute : Route
