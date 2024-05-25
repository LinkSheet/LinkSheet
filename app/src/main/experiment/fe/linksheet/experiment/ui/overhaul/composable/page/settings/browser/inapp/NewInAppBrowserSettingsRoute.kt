package fe.linksheet.experiment.ui.overhaul.composable.page.settings.browser.inapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.list.base.ContentPosition
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ListItemFilledIconButton
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.PreferenceRadioButtonListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.RadioButtonListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.inAppBrowserSettingsDisableInSelectedRoute
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.viewmodel.InAppBrowserSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewInAppBrowserSettingsRoute(
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
                    preference = viewModel.inAppBrowserMode,
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
                    preference = viewModel.inAppBrowserMode,
                    position = ContentPosition.Leading,
                    headlineContent = textContent(R.string.always_disable),
                    supportingContent = textContent(R.string.always_disable_explainer)
                )
            }

            item(key = R.string.disable_in_selected) { padding, shape ->
                RadioButtonListItem(
                    shape = shape,
                    padding = padding,
                    selected = viewModel.inAppBrowserMode() == InAppBrowserHandler.InAppBrowserMode.DisableInSelectedApps,
                    onSelect = { viewModel.inAppBrowserMode(InAppBrowserHandler.InAppBrowserMode.DisableInSelectedApps) },
                    position = ContentPosition.Leading,
                    headlineContent = textContent(R.string.disable_in_selected),
                    supportingContent = textContent(R.string.disable_in_selected_explainer),
                    otherContent = {
                        ListItemFilledIconButton(
                            imageVector = Icons.Outlined.Settings,
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


