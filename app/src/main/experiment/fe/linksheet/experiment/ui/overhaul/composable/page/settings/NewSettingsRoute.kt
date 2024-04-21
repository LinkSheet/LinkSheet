package fe.linksheet.experiment.ui.overhaul.composable.page.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import fe.linksheet.*
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.RouteNavigateListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.default.DefaultClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.RouteNavItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.group
import fe.linksheet.module.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

internal typealias Nav = RouteNavItem

internal object NewSettingsRouteData {
    val customization = arrayOf(
        Nav(appsSettingsRoute, Icons.Default.Apps, R.string.apps, R.string.apps_explainer),
        Nav(browserSettingsRoute, Icons.Default.OpenInBrowser, R.string.browser, R.string.browser_explainer),
        Nav(
            bottomSheetSettingsRoute,
            Icons.Default.ArrowUpward,
            R.string.bottom_sheet,
            R.string.bottom_sheet_explainer
        ),
        Nav(linksSettingsRoute, Icons.Default.Link, R.string.links, R.string.links_explainer)
    )

    val miscellaneous = arrayOf(
        Nav(generalSettingsRoute, Icons.Default.Settings, R.string.general, R.string.general_settings_explainer),
        Nav(
            notificationSettingsRoute,
            Icons.Default.Notifications,
            R.string.notifications,
            R.string.notifications_explainer
        ),
        Nav(themeSettingsRoute, Icons.Default.DisplaySettings, R.string.theme, R.string.theme_explainer),
        Nav(privacySettingsRoute, Icons.Default.PrivacyTip, R.string.privacy, R.string.privacy_settings_explainer)
    )

    val advanced = arrayOf(
        Nav(advancedSettingsRoute, Icons.Default.Adb, R.string.advanced, R.string.advanced_explainer),
        Nav(debugSettingsRoute, Icons.Default.BugReport, R.string.debug, R.string.debug_explainer)
    )

    val dev = Nav(devModeRoute, Icons.Default.DeveloperMode, R.string.dev, R.string.dev_explainer)
    val about = Nav(aboutSettingsRoute, Icons.Default.Info, R.string.about, R.string.about_explainer)
}

@Composable
fun NewSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val devMode = viewModel.devModeEnabled()

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.settings), onBackPressed = onBackPressed) {
        divider(stringRes = R.string.customization)

        group(items = NewSettingsRouteData.customization) { data, padding, shape ->
            RouteNavigateListItem(data = data, padding = padding, shape = shape, navigate = navigate)
        }

        divider(stringRes = R.string.misc_settings)

        group(items = NewSettingsRouteData.miscellaneous) { data, padding, shape ->
            RouteNavigateListItem(data = data, padding = padding, shape = shape, navigate = navigate)
        }

        divider(stringRes = R.string.advanced)

        group(size = NewSettingsRouteData.advanced.size + if (devMode) 1 else 0) {
            items(values = NewSettingsRouteData.advanced) { data, padding, shape ->
                RouteNavigateListItem(data = data, padding = padding, shape = shape, navigate = navigate)
            }

            if (devMode) {
                item(key = NewSettingsRouteData.dev.route) { padding, shape ->
                    RouteNavigateListItem(
                        data = NewSettingsRouteData.dev,
                        padding = padding,
                        shape = shape,
                        navigate = navigate
                    )
                }
            }
        }

        divider(stringRes = R.string.about)

        item(key = NewSettingsRouteData.about.route, contentType = ContentTypeDefaults.SingleGroupItem) {
            RouteNavigateListItem(data = NewSettingsRouteData.about, navigate = navigate)
        }
    }
}
