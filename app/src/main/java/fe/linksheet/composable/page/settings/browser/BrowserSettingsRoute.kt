package fe.linksheet.composable.page.settings.browser

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.composekit.route.Route
import fe.linksheet.R
import fe.linksheet.navigation.PreferredBrowserSettingsRoute
import fe.linksheet.navigation.inAppBrowserSettingsRoute

@Composable
fun BrowserSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    navigateNew: (Route) -> Unit,
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
                    onClick = { navigateNew(PreferredBrowserSettingsRoute) }
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
}
