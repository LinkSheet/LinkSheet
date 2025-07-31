package fe.linksheet.composable.page.settings.browser

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.linksheet.R
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.navigation.inAppBrowserSettingsRoute
import fe.linksheet.navigation.preferredBrowserSettingsRoute


@Composable
fun BrowserSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
) {
    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.app_browsers), onBackPressed = onBackPressed) {
        group(size = 2) {
            item(key = R.string.browser_mode) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    headlineContent = textContent(R.string.browser_mode),
                    supportingContent = textContent(R.string.browser_mode_subtitle),
                    icon = Icons.Outlined.OpenInBrowser.iconPainter,
                    shape = shape,
                    padding = padding,
                    onClick = { navigate(preferredBrowserSettingsRoute) }
                )
            }

            item(key = R.string.in_app_browser) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    headlineContent = textContent(R.string.in_app_browser),
                    supportingContent = textContent(R.string.in_app_browser_subtitle),
                    icon = Icons.AutoMirrored.Outlined.OpenInNew.iconPainter,
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
//                    headlineContent = R.string.preferred_browser,
//                    supportingContent = R.string.preferred_browser_explainer,
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
//                    headlineContent = R.string.in_app_browser,
//                    supportingContent = R.string.in_app_browser_explainer,
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
