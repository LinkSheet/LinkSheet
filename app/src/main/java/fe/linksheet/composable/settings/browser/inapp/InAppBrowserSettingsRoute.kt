package fe.linksheet.composable.settings.browser.inapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.settings.browser.BrowserCommonPackageSelectorData
import fe.linksheet.composable.settings.browser.BrowserCommonRadioButtonRowData
import fe.linksheet.composable.settings.browser.BrowserCommonScaffold
import fe.linksheet.inAppBrowserSettingsDisableInSelectedRoute
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.viewmodel.InAppBrowserSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun InAppBrowserSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: InAppBrowserSettingsViewModel = koinViewModel()
) {
    val rows = remember {
        listOf(
            BrowserCommonRadioButtonRowData(
                R.string.use_app_settings,
                R.string.use_app_settings_explainer
            ),
            BrowserCommonRadioButtonRowData(
                R.string.always_disable,
                R.string.always_disable_explainer
            )
        )
    }

    BrowserCommonScaffold(
        navController = navController,
        headline = R.string.in_app_browser,
        explainer = R.string.in_app_browser_subtitle,
        onBackPressed = onBackPressed,
        viewModel = viewModel,
        values = listOf(
            InAppBrowserHandler.InAppBrowserMode.UseAppSettings,
            InAppBrowserHandler.InAppBrowserMode.AlwaysDisableInAppBrowser,
        ),
        state = viewModel.inAppBrowserMode,
        rowKey = { it.value },
        rows = rows,
        selectorData = BrowserCommonPackageSelectorData(
            R.string.disable_in_selected,
            R.string.disable_in_selected_explainer,
            InAppBrowserHandler.InAppBrowserMode.DisableInSelectedApps,
            inAppBrowserSettingsDisableInSelectedRoute
        )
    )
}


