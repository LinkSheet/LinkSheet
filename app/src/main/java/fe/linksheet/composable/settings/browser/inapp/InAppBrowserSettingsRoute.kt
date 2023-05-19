package fe.linksheet.composable.settings.browser.inapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.settings.browser.BrowserCommonDialog
import fe.linksheet.composable.settings.browser.BrowserCommonRadioButtonRowData
import fe.linksheet.composable.settings.browser.BrowserCommonScaffold
import fe.linksheet.composable.util.dialogHelper
import fe.linksheet.module.viewmodel.InAppBrowserDisableInSelected
import fe.linksheet.module.viewmodel.InAppBrowserSettingsViewModel
import fe.linksheet.resolver.InAppBrowserHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import org.koin.androidx.compose.koinViewModel

@Composable
fun InAppBrowserSettingsRoute(
    onBackPressed: () -> Unit,
    settingsViewModel: SettingsViewModel,
    viewModel: InAppBrowserSettingsViewModel = koinViewModel()
) {
    val dialog = dialogHelper<Unit, InAppBrowserDisableInSelected?, InAppBrowserDisableInSelected>(
        fetch = {
            viewModel.disableInAppBrowserInSelected
                .flowOn(Dispatchers.IO)
                .firstOrNull()
                ?.toMutableMap()
        },
        onClose = { closeState ->
            viewModel.saveInAppBrowserDisableInSelected(closeState!!)
        },
        awaitFetchBeforeOpen = false,
        notifyCloseNoState = false,
        dynamicHeight = true
    ) { state, close ->
        BrowserCommonDialog(
            title = R.string.in_app_browser,
            state = state,
            alwaysShowPackageName = settingsViewModel.alwaysShowPackageName.value,
            close = close
        )
    }

    val rows = remember {
        listOf(
            BrowserCommonRadioButtonRowData(
                InAppBrowserHandler.InAppBrowserMode.UseAppSettings,
                settingsViewModel.inAppBrowserMode,
                R.string.use_app_settings,
                R.string.use_app_settings_explainer
            ),
            BrowserCommonRadioButtonRowData(
                InAppBrowserHandler.InAppBrowserMode.AlwaysDisableInAppBrowser,
                settingsViewModel.inAppBrowserMode,
                R.string.always_disable,
                R.string.always_disable_explainer
            ),
            BrowserCommonRadioButtonRowData(
                InAppBrowserHandler.InAppBrowserMode.DisableInSelectedApps,
                settingsViewModel.inAppBrowserMode,
                R.string.disable_in_selected,
                R.string.disable_in_selected_explainer
            ) { dialog.open(Unit) }
        )
    }

    BrowserCommonScaffold(
        headline = R.string.in_app_browser,
        explainer = R.string.in_app_browser_explainer,
        onBackPressed = onBackPressed,
        viewModel = viewModel,
        rowKey = { it.value.value },
        rows = rows
    )
}


