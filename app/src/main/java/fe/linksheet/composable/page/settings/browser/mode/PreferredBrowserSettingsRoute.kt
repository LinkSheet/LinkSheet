package fe.linksheet.composable.page.settings.browser.mode

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.ListItemFilledIconButton
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.composekit.route.Route
import fe.linksheet.R
import fe.linksheet.composable.component.list.item.type.PreferenceRadioButtonListItem
import fe.linksheet.composable.util.FilterChipValue
import fe.linksheet.composable.util.FilterChips
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.module.viewmodel.PreferredBrowserViewModel
import fe.linksheet.module.viewmodel.PreferredBrowserViewModel.BrowserType
import fe.linksheet.navigation.SingleBrowserSettingsRoute
import fe.linksheet.navigation.WhitelistedBrowsersSettingsRoute
import org.koin.androidx.compose.koinViewModel

@Composable
fun PreferredBrowserSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (Route) -> Unit,
    viewModel: PreferredBrowserViewModel = koinViewModel(),
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.init()
    }

    val type by viewModel.type.collectAsStateWithLifecycle()
    val modePref by viewModel.browserMode.collectAsStateWithLifecycle(initialValue = null)
    val browserStatePref by viewModel.selectedBrowser.collectAsStateWithLifecycle(initialValue = null)
    val unifiedPreferredBrowser by viewModel.unifiedPreferredBrowser.collectAsStateWithLifecycle()

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.browser_mode), onBackPressed = onBackPressed) {
//        divider(id = R.string.browser_mode_subtitle)

        item(key = R.string.browser_mode_subtitle, contentType = ContentType.SingleGroupItem) {
            PreferenceSwitchListItem(
                statePreference = viewModel.unifiedPreferredBrowser,
                headlineContent = textContent(R.string.use_unified_preferred_browser),
                supportingContent = textContent(R.string.use_unified_preferred_browser_explainer),
            )

            if (!unifiedPreferredBrowser) {
                FilterChips(
                    currentState = type,
                    onClick = { viewModel.type.value = it },
                    values = listOf(
                        FilterChipValue(
                            BrowserType.Normal,
                            R.string.normal
                        ),
                        FilterChipValue(
                            BrowserType.InApp,
                            R.string.in_app
                        )
                    )
                )
            }
        }

        divider(id = R.string.browser_mode_divider)

        group(size = 4) {
            item(key = R.string.always_ask) { padding, shape ->
                modePref?.let {
                    PreferenceRadioButtonListItem(
                        padding = padding,
                        shape = shape,
                        value = BrowserMode.AlwaysAsk,
                        statePreference = it,
                        position = ContentPosition.Leading,
                        headlineContent = textContent(R.string.always_ask),
                        supportingContent = textContent(R.string.always_ask_explainer)
                    )
                }
            }

            item(key = R.string.none) { padding, shape ->
                modePref?.let {
                    PreferenceRadioButtonListItem(
                        padding = padding,
                        shape = shape,
                        value = BrowserMode.None,
                        statePreference = it,
                        position = ContentPosition.Leading,
                        headlineContent = textContent(R.string.none),
                        supportingContent = textContent(R.string.none_explainer)
                    )
                }
            }

            item(key = R.string.whitelisted) { padding, shape ->
                modePref?.let {
                    PreferenceRadioButtonListItem(
                        padding = padding,
                        shape = shape,
                        value = BrowserMode.Whitelisted,
                        statePreference = it,
                        position = ContentPosition.Leading,
                        headlineContent = textContent(R.string.whitelisted),
                        supportingContent = textContent(R.string.whitelisted_explainer),
                        otherContent = {
                            ListItemFilledIconButton(
                                iconPainter = Icons.Outlined.Settings.iconPainter,
                                contentDescription = stringResource(id = R.string.whitelisted),
                                onClick = { navigate(WhitelistedBrowsersSettingsRoute(type)) }
                            )
                        }
                    )
                }
            }

            item(key = R.string.settings_apps_browsers_mode__title_selected) { padding, shape ->
                modePref?.let {
                    val browserState by browserStatePref!!.collectAsStateWithLifecycle()
                    val appInfo = viewModel.getAppInfo(browserState)

                    PreferenceRadioButtonListItem(
                        padding = padding,
                        shape = shape,
                        value = BrowserMode.SelectedBrowser,
                        statePreference = it,
                        position = ContentPosition.Leading,
                        headlineContent = textContent(R.string.settings_apps_browsers_mode__title_selected),
                        supportingContent = when (appInfo) {
                            null -> textContent(R.string.settings_apps_browsers_mode__text_none)
                            else -> text(appInfo.appInfo.label)
                        },
                        otherContent = {
                            ListItemFilledIconButton(
                                iconPainter = Icons.Outlined.Settings.iconPainter,
                                contentDescription = stringResource(id = R.string.settings_apps_browsers_mode__title_selected),
                                onClick = { navigate(SingleBrowserSettingsRoute(type)) }
                            )
                        }
                    )
                }
            }
        }
    }
}
