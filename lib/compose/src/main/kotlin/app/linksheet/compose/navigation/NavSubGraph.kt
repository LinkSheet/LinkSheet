package app.linksheet.compose.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation

interface NavSubGraph {
    val startDestination: Any
    val graph: NavGraphBuilder.(NavHostController) -> Unit
}

inline fun <reified T : NavSubGraph> NavGraphBuilder.attachSubGraph(
    page: T,
    navController: NavHostController,
) {
    navigation<T>(startDestination = page.startDestination) {
        page.graph(this, navController)
    }
}
