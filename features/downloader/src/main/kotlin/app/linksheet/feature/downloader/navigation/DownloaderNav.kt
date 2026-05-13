package app.linksheet.feature.downloader.navigation

import androidx.annotation.Keep
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import app.linksheet.compose.util.animatedComposable
import app.linksheet.feature.downloader.ui.DownloaderSettings
import fe.composekit.route.Nav
import fe.composekit.route.Route
import kotlinx.serialization.Serializable

@Serializable
object DownloaderNav : Nav {
    override val graph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        animatedComposable<DownloaderRoute> { _, route ->
            DownloaderSettings(onBackPressed = navController::popBackStack)
        }
    }
}

@Keep
@Serializable
data object DownloaderRoute : Route {
}
