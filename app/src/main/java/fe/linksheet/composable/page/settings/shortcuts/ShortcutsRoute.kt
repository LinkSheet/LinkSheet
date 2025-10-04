package fe.linksheet.composable.page.settings.shortcuts

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.linksheet.R
import app.linksheet.compose.page.SaneScaffoldSettingsPage


internal object NewSettingsRouteData {

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
                DefaultTwoLineIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.settings_shortcuts__title_default_browser),
                    supportingContent = textContent(R.string.settings_shortcuts__subtitle_default_browser),
                    onClick = {

                    }
                )
            }

            item(key = R.string.settings_shortcuts__title_link_handlers) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.settings_shortcuts__title_link_handlers),
                    supportingContent = textContent(R.string.settings_shortcuts__subtitle_default_browser),
                    onClick = {

                    }
                )
            }

            item(key = R.string.settings_shortcuts__title_connected_apps) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
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
