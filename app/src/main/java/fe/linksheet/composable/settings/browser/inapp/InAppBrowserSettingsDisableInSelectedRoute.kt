package fe.linksheet.composable.settings.browser.inapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.settings.browser.BrowserCommonPackageSelectorData
import fe.linksheet.composable.settings.browser.BrowserCommonPackageSelectorRoute
import fe.linksheet.composable.settings.browser.BrowserCommonRadioButtonRowData
import fe.linksheet.composable.settings.browser.BrowserCommonScaffold
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.viewmodel.InAppBrowserSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun InAppBrowserSettingsDisableInSelectedRoute(
    navController: NavHostController,
    viewModel: InAppBrowserSettingsViewModel = koinViewModel()
) {
    BrowserCommonPackageSelectorRoute(
        headlineId = R.string.disable_in_selected,
        subtitleId = R.string.disable_in_selected_explainer,
        noItemsId = R.string.no_apps_found,
        navController = navController,
        viewModel = viewModel
    )
}


