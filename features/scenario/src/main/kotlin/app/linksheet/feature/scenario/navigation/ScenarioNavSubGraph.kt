package app.linksheet.feature.scenario.navigation

import androidx.annotation.Keep
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import app.linksheet.compose.navigation.NavSubGraph
import app.linksheet.compose.util.animatedComposable
import app.linksheet.feature.scenario.ui.ScenarioRoute
import fe.composekit.route.Route
import kotlinx.serialization.Serializable

@Serializable
object ScenarioNavSubGraph : NavSubGraph {
    override val startDestination: Any = ScenarioRoute
    override val graph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        animatedComposable<ScenarioRoute> { _, route ->
            ScenarioRoute(onBackPressed = navController::popBackStack)
        }
    }
}

@Keep
@Serializable
data object ScenarioRoute : Route {
//    val NavItem by lazy {
//        RouteNavItemNew(
//            this,
//            Icons.Outlined.Adb.iconPainter,
//            textContent(R.string.settings_shizuku__title_shizuku),
//            textContent(R.string.settings_shizuku__text_shizuku)
//        )
//    }
}

