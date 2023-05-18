package fe.linksheet.composable.settings.browser

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.tasomaniac.openwith.resolver.BrowserHandler
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.util.LaunchedEffectOnFirstAndResume
import fe.linksheet.composable.util.RadioButtonRow
import fe.linksheet.composable.util.dialogHelper
import fe.linksheet.extension.CurrentActivity
import fe.linksheet.extension.startPackageInfoActivity
import kotlinx.coroutines.launch

@Composable
fun PreferredBrowserSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel
) {
    val activity = LocalContext.CurrentActivity()

    LaunchedEffectOnFirstAndResume {
        viewModel.loadBrowsers()
    }

    val coroutineScope = rememberCoroutineScope()

    val (dialogOpen, dialogContentLoaded) = BrowserCommonDialog(
        title = R.string.whitelisted_browsers,
        alwaysShowPackageName = viewModel.alwaysShowPackageName.value,
        items = viewModel.whitelistedBrowserMap,
        updateActivityState = { activity, state ->
            viewModel.whitelistedBrowserMap[activity] = state
        },
        closeAndSave = {
            coroutineScope.launch {
                viewModel.saveWhitelistedBrowsers()
            }
        }
    )

    BrowserCommonScaffold(
        headline = R.string.preferred_browser,
        explainer = R.string.preferred_browser_explainer,
        onBackPressed = onBackPressed,
        viewModel = viewModel,
        rowKey = { it.value.value },
        rows = listOf(
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
                coroutineScope.launch {
                    viewModel.queryWhitelistedBrowsersAsync()
                    dialogContentLoaded.value = true
                }

                dialogOpen.value = true
            }
        )
    ) {
        items(items = viewModel.browsers, key = { it.flatComponentName }) { app ->
            val selected = viewModel.browserMode.matches(BrowserHandler.BrowserMode.SelectedBrowser)
                    && viewModel.selectedBrowser.matches(app.packageName)
            RadioButtonRow(
                selected = selected,
                onClick = {
                    viewModel.updateBrowserMode(BrowserHandler.BrowserMode.SelectedBrowser)
                    viewModel.updateState(viewModel.selectedBrowser, app.packageName)
                },
                onLongClick = {
                    activity.startPackageInfoActivity(app)
                }
            ) {
                BrowserIconTextRow(
                    context = activity,
                    app = app,
                    selected = selected,
                    showSelectedText = true,
                    alwaysShowPackageName = viewModel.alwaysShowPackageName.value
                )
            }
        }
    }
}