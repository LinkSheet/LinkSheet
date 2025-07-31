package fe.linksheet.composable.page.settings.browser.inapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.ListItemFilledIconButton
import fe.composekit.component.list.item.type.RadioButtonListItem
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.R
import fe.linksheet.composable.component.list.item.type.PreferenceRadioButtonListItem
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.navigation.inAppBrowserSettingsDisableInSelectedRoute
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.viewmodel.InAppBrowserSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun InAppBrowserSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    viewModel: InAppBrowserSettingsViewModel = koinViewModel(),
) {
    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.in_app_browser), onBackPressed = onBackPressed) {
        group(size = 3) {
            item(key = R.string.use_app_settings) { padding, shape ->
                PreferenceRadioButtonListItem(
                    padding = padding,
                    shape = shape,
                    value = InAppBrowserHandler.InAppBrowserMode.UseAppSettings,
                    statePreference = viewModel.inAppBrowserMode,
                    position = ContentPosition.Leading,
                    headlineContent = textContent(R.string.use_app_settings),
                    supportingContent = textContent(R.string.use_app_settings_explainer)
                )
            }

            item(key = R.string.always_disable) { padding, shape ->
                PreferenceRadioButtonListItem(
                    padding = padding,
                    shape = shape,
                    value = InAppBrowserHandler.InAppBrowserMode.AlwaysDisableInAppBrowser,
                    statePreference = viewModel.inAppBrowserMode,
                    position = ContentPosition.Leading,
                    headlineContent = textContent(R.string.always_disable),
                    supportingContent = textContent(R.string.always_disable_explainer)
                )
            }

            item(key = R.string.disable_in_selected) { padding, shape ->
                val inAppBrowserMode by viewModel.inAppBrowserMode.collectAsStateWithLifecycle()

                RadioButtonListItem(
                    shape = shape,
                    padding = padding,
                    selected = inAppBrowserMode == InAppBrowserHandler.InAppBrowserMode.DisableInSelectedApps,
                    onSelect = { viewModel.inAppBrowserMode(InAppBrowserHandler.InAppBrowserMode.DisableInSelectedApps) },
                    position = ContentPosition.Leading,
                    headlineContent = textContent(R.string.disable_in_selected),
                    supportingContent = textContent(R.string.disable_in_selected_explainer),
                    otherContent = {
                        ListItemFilledIconButton(
                            iconPainter = Icons.Outlined.Settings.iconPainter,
                            contentDescription = stringResource(id = R.string.disable_in_selected),
                            onClick = { navigate(inAppBrowserSettingsDisableInSelectedRoute) }
                        )
                    }
                )
            }
        }
    }


//    BrowserCommonScaffold(
//        navController = navController,
//        headline = R.string.in_app_browser,
//        explainer = R.string.in_app_browser_explainer,
//        onBackPressed = onBackPressed,
//        viewModel = viewModel,
//        values = listOf(
//            InAppBrowserHandler.InAppBrowserMode.UseAppSettings,
//            InAppBrowserHandler.InAppBrowserMode.AlwaysDisableInAppBrowser,
//        ),
//        state = viewModel.inAppBrowserMode,
//        rowKey = { it.value },
//        rows = rows,
//        selectorData = BrowserCommonPackageSelectorData(
//            R.string.disable_in_selected,
//            R.string.disable_in_selected_explainer,
//            InAppBrowserHandler.InAppBrowserMode.DisableInSelectedApps,
//            inAppBrowserSettingsDisableInSelectedRoute
//        )
//    )
}


