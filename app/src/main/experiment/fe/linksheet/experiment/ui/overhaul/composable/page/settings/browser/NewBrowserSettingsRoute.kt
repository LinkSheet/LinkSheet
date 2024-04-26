package fe.linksheet.experiment.ui.overhaul.composable.page.settings.browser

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.outlined.AppShortcut
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.default.DefaultLeadingIconClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.inAppBrowserSettingsRoute
import fe.linksheet.preferredAppsSettingsRoute
import fe.linksheet.preferredBrowserSettingsRoute


@Composable
fun NewBrowserSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
) {
    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.app_browsers), onBackPressed = onBackPressed) {
        group(size = 3) {
            item(key = R.string.default_apps) { padding, shape ->
                DefaultLeadingIconClickableShapeListItem(
                    headlineId = R.string.default_apps,
                    subtitleId = R.string.default_apps_subtitle,
                    icon = Icons.Outlined.AppShortcut,
                    shape = shape,
                    padding = padding,
                    onClick = { navigate(preferredAppsSettingsRoute) }
                )
            }

            item(key = R.string.browser_mode) { padding, shape ->
                DefaultLeadingIconClickableShapeListItem(
                    headlineId = R.string.browser_mode,
                    subtitleId = R.string.browser_mode_subtitle,
                    icon = Icons.Outlined.OpenInBrowser,
                    shape = shape,
                    padding = padding,
                    onClick = { navigate(preferredBrowserSettingsRoute) }
                )
            }

            item(key = R.string.in_app_browser) { padding, shape ->
                DefaultLeadingIconClickableShapeListItem(
                    headlineId = R.string.in_app_browser,
                    subtitleId = R.string.in_app_browser_subtitle,
                    icon = Icons.Outlined.OpenInNew,
                    shape = shape,
                    padding = padding,
                    onClick = { navigate(inAppBrowserSettingsRoute) }
                )
            }
        }
    }


//    SettingsScaffold(R.string.browser, onBackPressed = onBackPressed) { padding ->
//        LazyColumn(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxHeight(), contentPadding = PaddingValues(horizontal = 5.dp)
//        ) {
//            item(key = preferredBrowserSettingsRoute) {
//                SettingsItemRow(
//                    navController = navController,
//                    navigateTo = preferredBrowserSettingsRoute,
//                    headlineId = R.string.preferred_browser,
//                    subtitleId = R.string.preferred_browser_explainer,
//                    image = {
//                        ColoredIcon(
//                            icon = Icons.Default.OpenInBrowser,
//                            descriptionId = R.string.preferred_browser
//                        )
//                    }
//                )
//            }
//
//            item(key = inAppBrowserSettingsRoute) {
//                SettingsItemRow(
//                    navController = navController,
//                    navigateTo = inAppBrowserSettingsRoute,
//                    headlineId = R.string.in_app_browser,
//                    subtitleId = R.string.in_app_browser_explainer,
//                    image = {
//                        ColoredIcon(
//                            icon = Icons.Default.ExitToApp,
//                            descriptionId = R.string.in_app_browser
//                        )
//                    }
//                )
//            }
//        }
//    }
}
