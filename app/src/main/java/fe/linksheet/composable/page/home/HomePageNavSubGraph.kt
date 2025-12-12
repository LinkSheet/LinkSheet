package fe.linksheet.composable.page.home

import androidx.annotation.Keep
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import app.linksheet.compose.util.animatedComposable
import fe.composekit.route.NavSubGraph
import fe.composekit.route.Route
import fe.linksheet.composable.page.home.edit.TextEditorPageWrapper
import kotlinx.serialization.Serializable

@Serializable
object HomePageNavSubGraph : NavSubGraph<MainOverviewRoute> {
    override val startDestination = MainOverviewRoute
    override val graph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        animatedComposable<MainOverviewRoute> { _, route ->
            NewMainRoute(navController = navController)
        }

        animatedComposable<TextEditorRoute> { _, route ->
            TextEditorPageWrapper(
                initialText = route.text,
                popBackStack = navController::popBackStack
            )
        }
    }
}

@Serializable
data object MainOverviewRoute : Route

@Keep
@Serializable
data class TextEditorRoute(val text: String) : Route
