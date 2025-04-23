package fe.linksheet.composable.page.settings.advanced

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.layout.column.group
import fe.composekit.route.Route
import fe.composekit.route.RouteNavItemNew
import fe.composekit.route.RouteNavigateListItemNew
import fe.linksheet.R
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.navigation.ExperimentRoute
import fe.linksheet.navigation.ExportImportRoute

private object NewAdvancedSettingsRouteData {
    val items = arrayOf(
        RouteNavItemNew(
            ExperimentRoute.Empty,
            Icons.Outlined.Science.iconPainter,
            textContent(R.string.experiments),
            textContent(R.string.experiments_explainer),
        ),
        RouteNavItemNew(
            ExportImportRoute,
            Icons.Outlined.ImportExport.iconPainter,
            textContent(R.string.export_import_settings),
            textContent(R.string.export_import_settings_explainer),
        )
    )
}

@Composable
fun NewAdvancedSettingsRoute(onBackPressed: () -> Unit, navigate: (Route) -> Unit) {
    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.advanced),
        onBackPressed = onBackPressed
    ) {
        group(array = NewAdvancedSettingsRouteData.items) { data, padding, shape ->
            RouteNavigateListItemNew(
                data = data,
                shape = shape,
                padding = padding,
                navigate = navigate,
            )
        }
    }
}
