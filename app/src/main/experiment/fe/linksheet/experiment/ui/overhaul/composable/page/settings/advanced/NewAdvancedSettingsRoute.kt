package fe.linksheet.experiment.ui.overhaul.composable.page.settings.advanced

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.component.list.item.RouteNavigateListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.component.list.item.RouteNavItem
import fe.linksheet.component.page.layout.group
import fe.linksheet.component.util.ImageVectorIconType.Companion.vector
import fe.linksheet.component.util.Resource.Companion.textContent
import fe.linksheet.experimentSettingsRoute
import fe.linksheet.exportImportSettingsRoute
import fe.linksheet.featureFlagSettingsRoute


private object NewAdvancedSettingsRouteData {
    val items = arrayOf(
        RouteNavItem(
            featureFlagSettingsRoute,
            vector(Icons.Outlined.Flag),
            textContent(R.string.feature_flags),
            textContent(R.string.feature_flags_explainer),
        ),
        RouteNavItem(
            experimentSettingsRoute.route,
            vector(Icons.Outlined.Science),
            textContent(R.string.experiments),
            textContent(R.string.experiments_explainer),
        ),
        RouteNavItem(
            exportImportSettingsRoute,
            vector(Icons.Outlined.ImportExport),
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
