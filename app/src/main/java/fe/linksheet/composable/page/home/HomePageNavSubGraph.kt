package fe.linksheet.composable.page.home

import androidx.annotation.Keep
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import app.linksheet.compose.util.animatedComposable
import fe.composekit.route.NavSubGraph
import fe.composekit.route.Route
import fe.linksheet.composable.page.home.edit.TextEditorPageWrapper
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
@SerialName("HomePageNavSubGraph")
object HomePageNavSubGraph : NavSubGraph<MainOverviewRoute> {
    override val startDestination = MainOverviewRoute
    override val graph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        animatedComposable<MainOverviewRoute> { _, route ->
            MainRoute(navController = navController)
        }

        animatedComposable<TextEditorRoute> { _, route ->
            TextEditorPageWrapper(
                initialText = route.text,
                popBackStack = navController::popBackStack
            )
        }
    }
}

@Keep
@Serializable
@SerialName("MainOverviewRoute")
data object MainOverviewRoute : Route

@Keep
@Serializable
@SerialName("TextEditorRoute")
data class TextEditorRoute(val text: String) : Route
