package fe.linksheet.composable.page.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.RouteNavItem
import fe.composekit.component.list.item.RouteNavigateListItem
import fe.composekit.layout.column.group
import fe.linksheet.*
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.module.viewmodel.SettingsViewModel
import fe.linksheet.navigation.aboutSettingsRoute
import fe.linksheet.navigation.advancedSettingsRoute
import fe.linksheet.navigation.appsWhichCanOpenLinksSettingsRoute
import fe.linksheet.navigation.bottomSheetSettingsRoute
import fe.linksheet.navigation.browserSettingsRoute
import fe.linksheet.navigation.debugSettingsRoute
import fe.linksheet.navigation.devModeRoute
import fe.linksheet.navigation.generalSettingsRoute
import fe.linksheet.navigation.linksSettingsRoute
import fe.linksheet.navigation.notificationSettingsRoute
import fe.linksheet.navigation.privacySettingsRoute
import fe.linksheet.navigation.themeSettingsRoute
import org.koin.androidx.compose.koinViewModel

internal object NewSettingsRouteData {
    val verifiedApps = RouteNavItem(
        appsWhichCanOpenLinksSettingsRoute,
        Icons.Outlined.DomainVerification.iconPainter,
        textContent(R.string.verified_link_handlers),
        textContent(R.string.verified_link_handlers_subtitle)
    )

    val customization = arrayOf(
        RouteNavItem(
            browserSettingsRoute,
            Icons.Outlined.Apps.iconPainter,
            textContent(R.string.app_browsers),
            textContent(R.string.app_browsers_subtitle),
        ),
        RouteNavItem(
            bottomSheetSettingsRoute,
            Icons.Outlined.SwipeUp.iconPainter,
            textContent(R.string.bottom_sheet),
            textContent(R.string.bottom_sheet_explainer),
        ),
        RouteNavItem(
            linksSettingsRoute,
            Icons.Outlined.Link.iconPainter,
            textContent(R.string.links),
            textContent(R.string.links_explainer),
        )
    )

    val miscellaneous = arrayOf(
        RouteNavItem(
            generalSettingsRoute,
            Icons.Outlined.Settings.iconPainter,
            textContent(R.string.general),
            textContent(R.string.general_settings_explainer),
        ),
        RouteNavItem(
            notificationSettingsRoute,
            Icons.Outlined.Notifications.iconPainter,
            textContent(R.string.notifications),
            textContent(R.string.notifications_explainer),
        ),
        RouteNavItem(
            themeSettingsRoute,
            Icons.Outlined.Palette.iconPainter,
            textContent(R.string.theme),
            textContent(R.string.theme_explainer),
        ),
        RouteNavItem(
            privacySettingsRoute,
            Icons.Outlined.PrivacyTip.iconPainter,
            textContent(R.string.privacy),
            textContent(R.string.privacy_settings_explainer),
        )
    )

    val advanced = arrayOf(
        RouteNavItem(
            advancedSettingsRoute,
            Icons.Outlined.Terminal.iconPainter,
            textContent(R.string.advanced),
            textContent(R.string.advanced_explainer),
        ),
        RouteNavItem(
            debugSettingsRoute,
            Icons.Outlined.BugReport.iconPainter,
            textContent(R.string.debug),
            textContent(R.string.debug_explainer),
        )
    )

    val dev = RouteNavItem(
        devModeRoute,
        Icons.Outlined.DeveloperMode.iconPainter,
        textContent(R.string.dev),
        textContent(R.string.dev_explainer)
    )

    val about = arrayOf(
//        RouteNavItem(
//            Routes.Help,
//            Icons.AutoMirrored.Outlined.HelpOutline.iconPainter,
//            textContent(R.string.help),
//            textContent(R.string.help_subtitle),
//        ),
//        RouteNavItem(
//            Routes.Shortcuts,
//            Icons.Outlined.SwitchAccessShortcut.iconPainter,
//            textContent(R.string.settings__title_shortcuts),
//            textContent(R.string.settings__subtitle_shortcuts),
//        ),
//        RouteNavItem(
//            Routes.Updates,
//            Icons.Outlined.Update.iconPainter,
//            textContent(R.string.settings__title_updates),
//            textContent(R.string.settings__subtitle_updates),
//        ),
        RouteNavItem(
            aboutSettingsRoute,
            Icons.Outlined.Info.iconPainter,
            textContent(R.string.about),
            textContent(R.string.about_explainer),
        )
    )
}

@Composable
fun NewSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val devMode = viewModel.devModeEnabled()

    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings),
        onBackPressed = onBackPressed
    ) {
        item(key = R.string.verified_link_handlers, contentType = ContentType.SingleGroupItem) {
            RouteNavigateListItem(data = NewSettingsRouteData.verifiedApps, navigate = navigate)
        }

        divider(id =  R.string.customization)

        group(array = NewSettingsRouteData.customization) { data, padding, shape ->
            RouteNavigateListItem(data = data, padding = padding, shape = shape, navigate = navigate)
        }

        divider(id =  R.string.misc_settings)

        group(array = NewSettingsRouteData.miscellaneous) { data, padding, shape ->
            RouteNavigateListItem(data = data, padding = padding, shape = shape, navigate = navigate)
        }

        divider(id =  R.string.advanced)

        group(size = NewSettingsRouteData.advanced.size + if (devMode) 1 else 0) {
            items(array = NewSettingsRouteData.advanced) { data, padding, shape ->
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

        divider(id =  R.string.about)

        group(array = NewSettingsRouteData.about) { data, padding, shape ->
            RouteNavigateListItem(data = data, padding = padding, shape = shape, navigate = navigate)
        }
    }
}
