package fe.linksheet.composable.page.settings

import app.linksheet.compose.page.SaneScaffoldSettingsPage
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.feature.engine.navigation.ScenarioOverviewRoute
import app.linksheet.feature.shizuku.navigation.ShizukuRoute
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.item.RouteNavItem
import fe.composekit.component.list.item.RouteNavigateListItem
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.composekit.layout.column.group
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.composekit.route.Route
import fe.composekit.route.RouteNavItemNew
import fe.composekit.route.RouteNavigateListItemNew
import fe.linksheet.R
import fe.linksheet.composable.page.settings.language.rememberLanguageDialog
import fe.linksheet.module.language.LocaleItem
import fe.linksheet.module.viewmodel.SettingsViewModel
import fe.linksheet.navigation.*
import org.koin.androidx.compose.koinViewModel

internal object SettingsRouteData {
    private val vlhNavItem = RouteNavItemNew(
        AppsWhichCanOpenLinksSettingsRoute,
        Icons.Outlined.DomainVerification.iconPainter,
        textContent(R.string.verified_link_handlers),
        textContent(R.string.verified_link_handlers_subtitle)
    )

    fun section1(newShizuku: Boolean, scenario: Boolean): List<RouteNavItemNew> {
        return listOfNotNull(
            vlhNavItem,
            if (newShizuku) ShizukuRoute.NavItem else null,
            if (scenario) ScenarioOverviewRoute.NavItem else null
        )
    }

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
        )
    )

    val languageRoute = RouteNavItemNew(
        LanguageRoute,
        Icons.Outlined.Language.iconPainter,
        textContent(R.string.settings_language__dialog_title),
        textContent(R.string.settings_language__text_system_language_with_language),
    )

    val privacyRoute = RouteNavItem(
        privacySettingsRoute,
        Icons.Outlined.PrivacyTip.iconPainter,
        textContent(R.string.privacy),
        textContent(R.string.privacy_settings_explainer),
    )

    val advanced = arrayOf(
        RouteNavItemNew(
            AdvancedRoute,
            Icons.Outlined.Terminal.iconPainter,
            textContent(R.string.advanced),
            textContent(R.string.settings__subtitle_advanced),
        ),
        RouteNavItemNew(
            DebugRoute,
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
fun SettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    navigateNew: (Route) -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val devMode by viewModel.devModeEnabled.collectAsStateWithLifecycle()
    val newShizuku by viewModel.newShizuku.collectAsStateWithLifecycle()
    val scenario by viewModel.scenario.collectAsStateWithLifecycle()
    val vlh = remember(newShizuku, scenario) {
        SettingsRouteData.section1(newShizuku, scenario)
    }

    val languageDialog = rememberLanguageDialog()


    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings),
        onBackPressed = onBackPressed
    ) {
        group(list = vlh) { data, padding, shape ->
            RouteNavigateListItemNew(data = data, padding = padding, shape = shape, navigate = navigateNew)
        }

        divider(id = R.string.customization)

        group(array = SettingsRouteData.customization) { data, padding, shape ->
            RouteNavigateListItem(data = data, padding = padding, shape = shape, navigate = navigate)
        }

        divider(id = R.string.misc_settings)

        group(size = SettingsRouteData.miscellaneous.size + 2) {
            items(array = SettingsRouteData.miscellaneous) { data, padding, shape ->
                RouteNavigateListItem(data = data, padding = padding, shape = shape, navigate = navigate)
            }

            item(key = SettingsRouteData.languageRoute.key) { padding, shape ->
                val appLocale by viewModel.currentLocale.collectAsStateWithLifecycle(
                    minActiveState = Lifecycle.State.RESUMED,
                    initialValue = null
                )
                LanguageListItem(
                    shape = shape,
                    padding = padding,
                    appLocale = appLocale?.first,
                    isSystemLanguage = appLocale?.second == true,
                    onClick = languageDialog::open
                )
            }

            item(key = privacySettingsRoute) { padding, shape ->
                RouteNavigateListItem(
                    data = SettingsRouteData.privacyRoute,
                    padding = padding,
                    shape = shape,
                    navigate = navigate
                )
            }
        }

        divider(id = R.string.advanced)

        group(size = SettingsRouteData.advanced.size + if (devMode) 1 else 0) {
            items(array = SettingsRouteData.advanced) { data, padding, shape ->
                RouteNavigateListItemNew(
                    data = data,
                    padding = padding,
                    shape = shape,
                    navigate = navigateNew
                )
            }

            if (devMode) {
                item(key = SettingsRouteData.dev.route) { padding, shape ->
                    RouteNavigateListItem(
                        data = SettingsRouteData.dev,
                        padding = padding,
                        shape = shape,
                        navigate = navigate
                    )
                }
            }
        }

        divider(id = R.string.about)

        group(array = SettingsRouteData.about) { data, padding, shape ->
            RouteNavigateListItem(data = data, padding = padding, shape = shape, navigate = navigate)
        }
    }
}

@Composable
private fun LanguageListItem(
    shape: Shape,
    padding: PaddingValues,
    appLocale: LocaleItem?,
    isSystemLanguage: Boolean,
    onClick: () -> Unit
) {
    DefaultTwoLineIconClickableShapeListItem(
        shape = shape,
        padding = padding,
        headlineContent = textContent(R.string.settings_language__dialog_title),
        supportingContent = when {
            appLocale != null && isSystemLanguage -> {
                textContent(
                    R.string.settings_language__text_system_language_with_language,
                    appLocale.displayName
                )
            }
            else -> text(appLocale?.displayName ?: "")
        },
        icon = Icons.Outlined.Language.iconPainter,
        onClick = onClick
    )
}
