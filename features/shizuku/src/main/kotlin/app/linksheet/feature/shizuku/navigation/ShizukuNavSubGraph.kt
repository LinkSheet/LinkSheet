package app.linksheet.feature.shizuku.navigation

import androidx.annotation.Keep
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Adb
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import app.linksheet.compose.util.animatedComposable
import app.linksheet.feature.shizuku.R
import app.linksheet.feature.shizuku.ui.ShizukuRoute
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.route.NavSubGraph
import fe.composekit.route.Route
import fe.composekit.route.RouteNavItemNew
import kotlinx.serialization.Serializable


@Serializable
object ShizukuNavSubGraph : NavSubGraph<ShizukuRoute> {
    override val startDestination = ShizukuRoute::class
    override val graph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        animatedComposable<ShizukuRoute> { _, route ->
            ShizukuRoute(onBackPressed = navController::popBackStack)
        }
    }
}

@Keep
@Serializable
data object ShizukuRoute : Route {
    val NavItem by lazy {
        RouteNavItemNew(
            this,
            Icons.Outlined.Adb.iconPainter,
            textContent(R.string.settings_shizuku__title_shizuku),
            textContent(R.string.settings_shizuku__text_shizuku)
        )
    }
}


