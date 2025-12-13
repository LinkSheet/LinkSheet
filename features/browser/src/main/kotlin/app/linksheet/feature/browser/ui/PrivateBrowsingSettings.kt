package app.linksheet.feature.browser.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import app.linksheet.compose.preview.PreviewContainer
import app.linksheet.feature.browser.R
import app.linksheet.feature.browser.navigation.PrivateBrowserBrowserRoute
import app.linksheet.feature.browser.viewmodel.PrivateBrowsingSettingsViewModel
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.column.shape.SelectableShapeListItem
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.SwitchListItem
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.composekit.route.Route
import org.koin.androidx.compose.koinViewModel


@Composable
fun PrivateBrowsingSettings(
    onBackPressed: () -> Unit,
    navigate: (Route) -> Unit,
    viewModel: PrivateBrowsingSettingsViewModel = koinViewModel(),
) {
    val enabled by viewModel.enabled.collectAsStateWithLifecycle()
    val allowedBrowsers by viewModel.allowedBrowsers.collectAsStateWithLifecycle(initialValue = emptySet())

    PrivateBrowsingSettingsRouteInternal(
        enabled = enabled,
        onEnable = viewModel.enabled,
        enabledCount = allowedBrowsers.size,
        navigate = navigate,
        onBackPressed = onBackPressed,
    )
}

@Composable
private fun PrivateBrowsingSettingsRouteInternal(
    enabled: Boolean,
    onEnable: (Boolean) -> Unit,
    enabledCount: Int,
    navigate: (Route) -> Unit,
    onBackPressed: () -> Unit,
) {
    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings_private_browsing__title_private_browsing),
        onBackPressed = onBackPressed
    ) {
        item(key = R.string.settings_private_browsing__title_enable_private_browsing, contentType = ContentType.SingleGroupItem) {
            SwitchListItem(
                checked = enabled,
                onCheckedChange = onEnable,
                position = ContentPosition.Trailing,
                headlineContent = textContent(R.string.settings_private_browsing__title_enable_private_browsing),
                supportingContent = textContent(
                    id = R.string.enable_request_private_browsing_button_explainer
                )
            )
        }

        divider(id = R.string.settings_private_browsing__divider_configuration)

        item(
            key = R.string.settings_private_browsing__title_allowed_browsers,
            contentType = ContentType.SingleGroupItem
        ) {
            SelectableShapeListItem(
                headlineContent = textContent(R.string.settings_private_browsing__title_allowed_browsers),
                supportingContent = textContent(
                    id = R.string.settings_private_browsing__text_allowed_browsers, enabledCount
                ),
                position = ContentPosition.Trailing,
                onClick = { navigate(PrivateBrowserBrowserRoute) }
            )
        }
    }
}

@Preview
@Composable
private fun PrivateBrowsingSettingsRoutePreview() {
    PrivateBrowsingSettingsRouteBase(
    )
}

@Preview
@Composable
private fun PrivateBrowsingSettingsRoutePreview2() {
    PrivateBrowsingSettingsRouteBase(

    )
}

@Composable
private fun PrivateBrowsingSettingsRouteBase() {
    PreviewContainer {
        PrivateBrowsingSettingsRouteInternal(
            enabled = false,
            onEnable = {

            },
            enabledCount = 1,
            navigate = {

            },
            onBackPressed = {},
        )
    }
}
