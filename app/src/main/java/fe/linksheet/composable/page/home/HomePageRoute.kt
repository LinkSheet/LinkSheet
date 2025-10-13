package fe.linksheet.composable.page.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import fe.linksheet.navigation.MainOverviewRoute
import fe.linksheet.navigation.TextEditorRoute
import fe.linksheet.composable.page.home.edit.TextEditorPageWrapper
import app.linksheet.compose.util.animatedComposable
import app.linksheet.compose.navigation.NavSubGraph
import kotlinx.serialization.Serializable

@Serializable
object HomePageRoute : NavSubGraph {

    override val startDestination: Any = MainOverviewRoute

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
