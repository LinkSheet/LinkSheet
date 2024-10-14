package fe.linksheet.composable.page.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import fe.linksheet.MainOverviewRoute
import fe.linksheet.TextEditorRoute
import fe.linksheet.composable.page.home.edit.TextEditorPageWrapper
import fe.linksheet.composable.util.animatedComposable
import fe.linksheet.navigation.PageRoute
import kotlinx.serialization.Serializable

@Serializable
object HomePageRoute : PageRoute {

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
