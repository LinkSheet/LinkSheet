package fe.linksheet.composable.settings.browser.inapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.settings.browser.BrowserCommonDialog
import fe.linksheet.composable.settings.browser.BrowserCommonRadioButtonRowData
import fe.linksheet.composable.settings.browser.BrowserCommonScaffold
import fe.linksheet.resolver.InAppBrowserHandler
import kotlinx.coroutines.launch

@Composable
fun InAppBrowserSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val (dialogOpen, dialogContentLoaded) = BrowserCommonDialog(
        title = R.string.in_app_browser,
        alwaysShowPackageName = viewModel.alwaysShowPackageName.value,
        items = viewModel.disableInAppBrowserInSelectedMap,
        updateActivityState = { activity, state ->
            viewModel.disableInAppBrowserInSelectedMap[activity] = state
        },
        closeAndSave = {
            coroutineScope.launch {
                viewModel.saveInAppBrowserDisableInSelected()
            }
        }
    )

    BrowserCommonScaffold(
        headline = R.string.in_app_browser,
        explainer = R.string.in_app_browser_explainer,
        onBackPressed = onBackPressed,
        viewModel = viewModel,
        rowKey = { it.value.value },
        rows = listOf(
            BrowserCommonRadioButtonRowData(
                InAppBrowserHandler.InAppBrowserMode.UseAppSettings,
                viewModel.inAppBrowserMode,
                R.string.use_app_settings,
                R.string.use_app_settings_explainer
            ),
            BrowserCommonRadioButtonRowData(
                InAppBrowserHandler.InAppBrowserMode.AlwaysDisableInAppBrowser,
                viewModel.inAppBrowserMode,
                R.string.always_disable,
                R.string.always_disable_explainer
            ),
            BrowserCommonRadioButtonRowData(
                InAppBrowserHandler.InAppBrowserMode.DisableInSelectedApps,
                viewModel.inAppBrowserMode,
                R.string.disable_in_selected,
                R.string.disable_in_selected_explainer
            ) {
                coroutineScope.launch {
                    viewModel.loadPackages(context)
                    viewModel.queryAppsForInAppBrowserDisableInSelected()

                    dialogContentLoaded.value = true
                }

                dialogOpen.value = true
            }
        )
    )
}


