package fe.linksheet.composable.settings.browser.mode

import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.tasomaniac.openwith.resolver.BrowserHandler
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.settings.browser.BrowserCommonDialog
import fe.linksheet.composable.settings.browser.BrowserCommonRadioButtonRowData
import fe.linksheet.composable.settings.browser.BrowserCommonScaffold
import fe.linksheet.composable.settings.browser.BrowserIconTextRow
import fe.linksheet.composable.util.RadioButtonRow
import fe.linksheet.composable.util.dialogHelper
import fe.linksheet.extension.currentActivity
import fe.linksheet.extension.ioState
import fe.linksheet.extension.startPackageInfoActivity
import fe.linksheet.module.viewmodel.PreferredBrowserViewModel
import fe.linksheet.module.viewmodel.WhitelistedBrowsers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import org.koin.androidx.compose.koinViewModel

@Composable
fun PreferredBrowserSettingsRoute(
    onBackPressed: () -> Unit,
    settingsViewModel: SettingsViewModel,
    viewModel: PreferredBrowserViewModel = koinViewModel()
) {
    val activity = LocalContext.currentActivity()
    val browsers by viewModel.browsers.ioState()

    val dialog = dialogHelper<Unit, WhitelistedBrowsers?, WhitelistedBrowsers>(
        fetch = {
            viewModel.whitelistedBrowsers
                .flowOn(Dispatchers.IO)
                .firstOrNull()
                ?.toMutableMap()
        },
        onClose = { closeState ->
            viewModel.saveWhitelistedBrowsers(closeState!!)
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
                BrowserHandler.BrowserMode.AlwaysAsk,
                viewModel.browserMode,
                R.string.always_ask,
                R.string.always_ask_explainer
            ),
            BrowserCommonRadioButtonRowData(
                BrowserHandler.BrowserMode.None,
                viewModel.browserMode,
                R.string.none,
                R.string.none_explainer
            ),
            BrowserCommonRadioButtonRowData(
                BrowserHandler.BrowserMode.Whitelisted,
                viewModel.browserMode,
                R.string.whitelisted,
                R.string.whitelisted_explainer
            ) {
                dialog.open(Unit)
            }
        )
    }

    BrowserCommonScaffold(
        headline = R.string.preferred_browser,
        explainer = R.string.preferred_browser_explainer,
        onBackPressed = onBackPressed,
        viewModel = viewModel,
        rowKey = { it.value.value },
        rows = rows
    ) {
        if (browsers == null) {
            item {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            items(items = browsers!!, key = { it.flatComponentName }) { app ->
                val selected =
                    viewModel.browserMode.matches(BrowserHandler.BrowserMode.SelectedBrowser)
                            && viewModel.selectedBrowser.matches(app.packageName)
                RadioButtonRow(
                    selected = selected,
                    onClick = {
                        viewModel.updateBrowserMode(BrowserHandler.BrowserMode.SelectedBrowser)
                        viewModel.updateState(
                            viewModel.selectedBrowser,
                            app.packageName
                        )
                    },
                    onLongClick = {
                        activity.startPackageInfoActivity(app)
                    }
                ) {
                    BrowserIconTextRow(
                        app = app,
                        selected = selected,
                        showSelectedText = true,
                        alwaysShowPackageName = settingsViewModel.alwaysShowPackageName.value
                    )
                }
            }
        }
    }
}