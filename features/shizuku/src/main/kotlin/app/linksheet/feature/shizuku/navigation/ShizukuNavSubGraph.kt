package app.linksheet.feature.shizuku.navigation

import androidx.annotation.Keep
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Adb
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import app.linksheet.compose.util.animatedComposable
import app.linksheet.feature.shizuku.R
import app.linksheet.feature.shizuku.ui.ShizukuSettings
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.route.NavSubGraph
import fe.composekit.route.Route
import fe.composekit.route.RouteNavItemNew
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
@SerialName("ShizukuNavSubGraph")
object ShizukuNavSubGraph : NavSubGraph<ShizukuRoute> {
    override val startDestination = ShizukuRoute
    override val graph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        animatedComposable<ShizukuRoute> { _, route ->
            ShizukuSettings(onBackPressed = navController::popBackStack)
        }
    }
}

@Keep
@Serializable
@SerialName("ShizukuRoute")
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


