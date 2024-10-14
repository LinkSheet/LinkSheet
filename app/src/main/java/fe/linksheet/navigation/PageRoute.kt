package fe.linksheet.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation

interface PageRoute {
    val startDestination: Any
    val graph: NavGraphBuilder.(NavHostController) -> Unit
}

inline fun <reified T : PageRoute> NavGraphBuilder.addPageRoute(
    page: T,
    navController: NavHostController,
) {
    navigation<T>(startDestination = page.startDestination) {
        page.graph(this, navController)
    }
}
