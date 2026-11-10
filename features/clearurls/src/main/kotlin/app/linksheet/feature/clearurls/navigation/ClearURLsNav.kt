package app.linksheet.feature.clearurls.navigation

import androidx.annotation.Keep
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import app.linksheet.compose.util.animatedComposable
import app.linksheet.feature.clearurls.ui.ClearURLsSettings
import fe.composekit.route.Nav
import fe.composekit.route.Route
import kotlinx.serialization.Serializable

@Serializable
object ClearURLsNav : Nav {
    override val graph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        animatedComposable<ClearURLsRoute> { _, route ->
            ClearURLsSettings(onBackPressed = navController::popBackStack)
        }
    }
}


@Keep
@Serializable
data object ClearURLsRoute : Route {
}
