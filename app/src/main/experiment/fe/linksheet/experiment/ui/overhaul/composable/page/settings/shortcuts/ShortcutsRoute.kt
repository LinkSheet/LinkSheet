package fe.linksheet.experiment.ui.overhaul.composable.page.settings.shortcuts

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.default.DefaultIconClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.component.util.Resource.Companion.textContent


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
