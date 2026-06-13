@file:OptIn(ExperimentalUuidApi::class)

package app.linksheet.feature.engine.navigation

import androidx.annotation.Keep
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Widgets
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import app.linksheet.compose.util.animatedComposable
import app.linksheet.feature.engine.R
import app.linksheet.feature.engine.ui.route.ScenarioOverviewRoute
import app.linksheet.feature.engine.ui.route.ScenarioRoute
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.route.NavSubGraph
import fe.composekit.route.Route
import fe.composekit.route.RouteNavItemNew
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@Keep
@Serializable
@SerialName("ScenarioNavSubGraph")
object ScenarioNavSubGraph : NavSubGraph<ScenarioOverviewRoute> {
    override val startDestination = ScenarioOverviewRoute
    override val graph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        animatedComposable<ScenarioOverviewRoute> { _, route ->
            ScenarioOverviewRoute(
                onBackPressed = navController::popBackStack,
                navigate = navController::navigate
            )
        }

        animatedComposable<ScenarioRoute> { _, route ->
            ScenarioRoute(onBackPressed = navController::popBackStack, id = route.id)
        }
    }
}

@Keep
@Serializable
@SerialName("ScenarioOverviewRoute")
data object ScenarioOverviewRoute : Route {
    val NavItem by lazy {
        RouteNavItemNew(
            this,
            Icons.Outlined.Widgets.iconPainter,
            textContent(R.string.settings_scenario__title_scenarios),
            textContent(R.string.settings_scenario__text_scenarios),
        )
    }
}

@Keep
@Serializable
@SerialName("ScenarioRoute")
data class ScenarioRoute(val id: Long) : Route
