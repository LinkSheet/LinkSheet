package app.linksheet.feature.libredirect.navigation

import androidx.annotation.Keep
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import app.linksheet.compose.navigation.NavSubGraph
import app.linksheet.compose.util.animatedComposable
import app.linksheet.feature.libredirect.ui.LibRedirectServiceSettingsRoute
import app.linksheet.feature.libredirect.ui.LibRedirectSettingsRoute
import fe.composekit.route.Route
import kotlinx.serialization.Serializable

@Serializable
object LibRedirectNavSubGraph : NavSubGraph {
    override val startDestination: Any = LibRedirectRoute
    override val graph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        animatedComposable<LibRedirectRoute> { _, route ->
            LibRedirectSettingsRoute(onBackPressed = navController::popBackStack, navigate = navController::navigate)
        }
        animatedComposable<LibRedirectServiceRoute> { _, route ->
            LibRedirectServiceSettingsRoute(onBackPressed = navController::popBackStack, serviceKey = route.serviceKey)
        }
    }
}

//    val NavItem by lazy {
//        RouteNavItemNew(
//            this,
//            Icons.Outlined.Adb.iconPainter,
//            textContent(R.string.settings_shizuku__title_shizuku),
//            textContent(R.string.settings_shizuku__text_shizuku)
//        )
//    }

@Keep
@Serializable
data object LibRedirectRoute : Route

@Keep
@Serializable
data class LibRedirectServiceRoute(val serviceKey: String) : Route
