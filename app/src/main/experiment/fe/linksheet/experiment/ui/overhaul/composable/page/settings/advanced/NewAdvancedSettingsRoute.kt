package fe.linksheet.experiment.ui.overhaul.composable.page.settings.advanced

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.item.RouteNavItem
import fe.composekit.component.list.item.RouteNavigateListItem
import fe.composekit.layout.column.group
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experimentSettingsRoute
import fe.linksheet.exportImportSettingsRoute
import fe.linksheet.featureFlagSettingsRoute


private object NewAdvancedSettingsRouteData {
    val items = arrayOf(
        RouteNavItem(
            featureFlagSettingsRoute,
            Icons.Outlined.Flag.iconPainter,
            textContent(R.string.feature_flags),
            textContent(R.string.feature_flags_explainer),
        ),
        RouteNavItem(
            experimentSettingsRoute.route,
            Icons.Outlined.Science.iconPainter,
            textContent(R.string.experiments),
            textContent(R.string.experiments_explainer),
        ),
        RouteNavItem(
            exportImportSettingsRoute,
            Icons.Outlined.ImportExport.iconPainter,
            textContent(R.string.export_import_settings),
            textContent(R.string.export_import_settings_explainer),
        )
    )
}

@Composable
fun NewAdvancedSettingsRoute(onBackPressed: () -> Unit, navigate: (String) -> Unit) {
    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.advanced),
        onBackPressed = onBackPressed
    ) {
        group(array = NewAdvancedSettingsRouteData.items) { data, padding, shape ->
            RouteNavigateListItem(
                data = data,
                shape = shape,
                padding = padding,
                navigate = navigate,
            )
        }
    }
}
