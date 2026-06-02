package app.linksheet.feature.backup.impl.navigation

import androidx.annotation.Keep
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImportExport
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import app.linksheet.compose.util.animatedComposable
import app.linksheet.feature.backup.impl.R
import app.linksheet.feature.backup.impl.ui.ExportImportSettingsRoute
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.route.Nav
import fe.composekit.route.Route
import fe.composekit.route.RouteNavItemNew
import kotlinx.serialization.Serializable

@Serializable
object BackupNav : Nav {
    override val graph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        animatedComposable<BackupRoute> { _, _ ->
            ExportImportSettingsRoute(onBackPressed = navController::popBackStack)
        }
    }
}

@Keep
@Serializable
data object BackupRoute : Route {
    val NavItem by lazy {
        RouteNavItemNew(
            this,
            Icons.Outlined.ImportExport.iconPainter,
            textContent(R.string.export_import_settings),
            textContent(R.string.export_import_settings_explainer),
        )
    }
}


