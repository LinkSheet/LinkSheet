package app.linksheet.compose.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation

interface NavSubGraph {
    val startDestination: Any
    val graph: NavGraphBuilder.(NavHostController) -> Unit
}

inline fun <reified G : NavSubGraph> NavGraphBuilder.attachSubGraph(
    page: G,
    navController: NavHostController,
) {
    navigation<G>(startDestination = page.startDestination) {
        page.graph(this, navController)
    }
}
