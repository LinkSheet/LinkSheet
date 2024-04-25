package fe.linksheet.experiment.ui.overhaul.composable.page.settings.browser.inapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.LeadingRadioButtonListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.RadioButtonListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.RadioLocation
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.preference.PreferenceLeadingRadioButtonListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.preference.PreferenceTrailingRadioButtonListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
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
                PreferenceLeadingRadioButtonListItem(
                    padding = padding,
                    shape = shape,
                    value = InAppBrowserHandler.InAppBrowserMode.UseAppSettings,
                    preference = viewModel.inAppBrowserMode,
                    headlineContentTextId = R.string.use_app_settings,
                    supportingContentTextId = R.string.use_app_settings_explainer
                )
            }

            item(key = R.string.always_disable) { padding, shape ->
                PreferenceLeadingRadioButtonListItem(
                    padding = padding,
                    shape = shape,
                    value = InAppBrowserHandler.InAppBrowserMode.AlwaysDisableInAppBrowser,
                    preference = viewModel.inAppBrowserMode,
                    headlineContentTextId = R.string.always_disable,
                    supportingContentTextId = R.string.always_disable_explainer
                )
            }

            item(key = R.string.disable_in_selected) { padding, shape ->
                RadioButtonListItem(
                    shape = shape,
                    padding = padding,
                    location = RadioLocation.Leading,
                    selected = viewModel.inAppBrowserMode() == InAppBrowserHandler.InAppBrowserMode.DisableInSelectedApps,
                    onClick = { viewModel.inAppBrowserMode(InAppBrowserHandler.InAppBrowserMode.DisableInSelectedApps) },
                    headlineContentText = stringResource(id = R.string.disable_in_selected),
                    supportingContentText = stringResource(id = R.string.disable_in_selected_explainer),
                    otherContent = {
                        Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                            FilledTonalIconButton(onClick = { navigate(inAppBrowserSettingsDisableInSelectedRoute) }) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = stringResource(id = R.string.disable_in_selected)
                                )
                            }
                        }
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


