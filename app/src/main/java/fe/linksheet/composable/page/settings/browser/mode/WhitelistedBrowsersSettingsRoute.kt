package fe.linksheet.composable.page.settings.browser.mode

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.page.settings.browser.BrowserCommonPackageSelectorRoute
import fe.linksheet.module.viewmodel.PreferredBrowserViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WhitelistedBrowsersSettingsRoute(
    navController: NavHostController,
    viewModel: PreferredBrowserViewModel = koinViewModel()
) {
    BrowserCommonPackageSelectorRoute(
        headlineId = R.string.whitelisted_browsers,
        subtitleId = R.string.whitelisted_explainer,
        noItemsId = R.string.no_browsers_found,
        navController = navController,
        viewModel = viewModel
    )
}


