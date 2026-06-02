package fe.linksheet.composable.page.settings.advanced

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import app.linksheet.feature.backup.impl.navigation.BackupRoute
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.layout.column.group
import fe.composekit.route.Route
import fe.composekit.route.RouteNavItemNew
import fe.composekit.route.RouteNavigateListItemNew
import fe.linksheet.R
import fe.linksheet.navigation.ExperimentRoute

private object AdvancedSettingsRouteData {
    val items = arrayOf(
        RouteNavItemNew(
            ExperimentRoute.Empty,
            Icons.Outlined.Science.iconPainter,
            textContent(R.string.experiments),
            textContent(R.string.experiments_explainer),
        ),
        BackupRoute.NavItem
    )
}

@Composable
fun AdvancedSettingsRoute(onBackPressed: () -> Unit, navigate: (Route) -> Unit) {
    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.advanced),
        onBackPressed = onBackPressed
    ) {
        group(array = AdvancedSettingsRouteData.items) { data, padding, shape ->
            RouteNavigateListItemNew(
                data = data,
                shape = shape,
                padding = padding,
                navigate = navigate,
            )
        }
    }
}
