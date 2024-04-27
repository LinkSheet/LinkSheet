package fe.linksheet.experiment.ui.overhaul.composable.page.settings.shortcuts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.linksheet.*
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.RouteNavItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.default.DefaultIconClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.component.util.Resource.Companion.textContent

internal typealias Nav = RouteNavItem

internal object NewSettingsRouteData {
    val verifiedApps = Nav(
        appsWhichCanOpenLinksSettingsRoute,
        Icons.Outlined.DomainVerification,
        R.string.verified_link_handlers,
        R.string.verified_link_handlers_subtitle
    )

    val customization = arrayOf(
        Nav(browserSettingsRoute, Icons.Outlined.Apps, R.string.app_browsers, R.string.app_browsers_subtitle),
        Nav(
            bottomSheetSettingsRoute,
            Icons.Outlined.SwipeUp,
            R.string.bottom_sheet,
            R.string.bottom_sheet_explainer
        ),
        Nav(linksSettingsRoute, Icons.Outlined.Link, R.string.links, R.string.links_explainer)
    )

    val miscellaneous = arrayOf(
        Nav(generalSettingsRoute, Icons.Outlined.Settings, R.string.general, R.string.general_settings_explainer),
        Nav(
            notificationSettingsRoute,
            Icons.Outlined.Notifications,
            R.string.notifications,
            R.string.notifications_explainer
        ),
        Nav(themeSettingsRoute, Icons.Outlined.Palette, R.string.theme, R.string.theme_explainer),
        Nav(privacySettingsRoute, Icons.Outlined.PrivacyTip, R.string.privacy, R.string.privacy_settings_explainer)
    )

    val advanced = arrayOf(
        Nav(advancedSettingsRoute, Icons.Outlined.Terminal, R.string.advanced, R.string.advanced_explainer),
        Nav(debugSettingsRoute, Icons.Outlined.BugReport, R.string.debug, R.string.debug_explainer)
    )

    val dev = Nav(devModeRoute, Icons.Outlined.DeveloperMode, R.string.dev, R.string.dev_explainer)
    val help = Nav(Routes.Help, Icons.AutoMirrored.Outlined.HelpOutline, R.string.help, R.string.help_subtitle)
    val shortcuts = Nav(
        Routes.Shortcuts,
        Icons.Outlined.SwitchAccessShortcut,
        R.string.settings__title_shortcuts,
        R.string.settings__subtitle_shortcuts
    )
    val updates = Nav(
        Routes.Updates,
        Icons.Outlined.Update,
        R.string.settings__title_updates,
        R.string.settings__subtitle_updates
    )

    val about = Nav(aboutSettingsRoute, Icons.Outlined.Info, R.string.about, R.string.about_explainer)
}

@Composable
fun ShortcutsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
) {
    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings__title_shortcuts),
        onBackPressed = onBackPressed
    ) {
        group(size = 3) {
            item(key = R.string.settings_shortcuts__title_default_browser) { padding, shape ->
                DefaultIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.settings_shortcuts__title_default_browser),
                    supportingContent = textContent(R.string.settings_shortcuts__subtitle_default_browser),
                    onClick = {

                    }
                )
            }

            item(key = R.string.settings_shortcuts__title_link_handlers) { padding, shape ->
                DefaultIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.settings_shortcuts__title_link_handlers),
                    supportingContent = textContent(R.string.settings_shortcuts__subtitle_default_browser),
                    onClick = {

                    }
                )
            }

            item(key = R.string.settings_shortcuts__title_connected_apps) { padding, shape ->
                DefaultIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.settings_shortcuts__title_connected_apps),
                    supportingContent = textContent(R.string.settings_shortcuts__subtitle_default_browser),
                    onClick = {

                    }
                )
            }
        }
    }
}
