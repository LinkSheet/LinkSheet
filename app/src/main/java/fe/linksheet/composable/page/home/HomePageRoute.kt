package fe.linksheet.composable.page.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import fe.linksheet.MainOverviewRoute
import fe.linksheet.TextEditorRoute
import fe.linksheet.composable.page.home.edit.TextEditorPageWrapper
import fe.linksheet.composable.page.main.NewMainRoute
import fe.linksheet.composable.util.animatedComposable
import kotlinx.serialization.Serializable

interface PageRoute {
    val startDestination: Any
    val graph: NavGraphBuilder.(NavHostController) -> Unit
}

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

inline fun <reified T : PageRoute> NavGraphBuilder.addPageRoute(
    page: T,
    navController: NavHostController,
) {
    navigation<T>(startDestination = page.startDestination) {
        page.graph(this, navController)
    }
}
