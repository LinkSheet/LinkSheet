package fe.linksheet.composable.settings.browser.mode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import fe.linksheet.module.resolver.BrowserHandler
import fe.linksheet.R
import fe.linksheet.composable.settings.browser.BrowserCommonDialog
import fe.linksheet.composable.settings.browser.BrowserCommonRadioButtonRowData
import fe.linksheet.composable.settings.browser.BrowserCommonScaffold
import fe.linksheet.composable.settings.browser.BrowserIconTextRow
import fe.linksheet.composable.util.FilterChipValue
import fe.linksheet.composable.util.FilterChips
import fe.linksheet.composable.util.RadioButtonRow
import fe.linksheet.composable.util.SwitchRow
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
import timber.log.Timber

@Composable
fun PreferredBrowserSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: PreferredBrowserViewModel = koinViewModel()
) {
    val activity = LocalContext.currentActivity()

    val browsers by viewModel.browsers.ioState()
    val type by viewModel.type.ioState()
    val browserMode by viewModel.browserModeState.ioState()
    val selectedBrowser by viewModel.selectedBrowserState.ioState()

    val dialog = dialogHelper<Unit, WhitelistedBrowsers?, WhitelistedBrowsers>(
        fetch = {
            viewModel.whitelistedBrowsers
                .flowOn(Dispatchers.IO)
                .firstOrNull()
                ?.firstOrNull()
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
            title = R.string.whitelisted_browsers,
            state = state,
            alwaysShowPackageName = viewModel.alwaysShowPackageName.value,
            close = close
        )
    }

    val rows = remember {
        listOf(
            BrowserCommonRadioButtonRowData(
                R.string.always_ask,
                R.string.always_ask_explainer
            ),
            BrowserCommonRadioButtonRowData(
                R.string.none,
                R.string.none_explainer
            ),
            BrowserCommonRadioButtonRowData(
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
        values = listOf(
            BrowserHandler.BrowserMode.AlwaysAsk,
            BrowserHandler.BrowserMode.None,
            BrowserHandler.BrowserMode.Whitelisted
        ),
        state = browserMode,
        rowKey = { it.value },
        rows = rows,
        header = {
            Column {
                SwitchRow(
                    checked = viewModel.unifiedPreferredBrowser.value,
                    onChange = { viewModel.updateState(viewModel.unifiedPreferredBrowser, it) },
                    headline = stringResource(id = R.string.use_unified_preferred_browser),
                )

                if (!viewModel.unifiedPreferredBrowser.value) {
                    FilterChips(
                        currentState = type,
                        onClick = { viewModel.type.value = it },
                        values = listOf(
                            FilterChipValue(PreferredBrowserViewModel.BrowserType.Normal, R.string.normal),
                            FilterChipValue(PreferredBrowserViewModel.BrowserType.InApp, R.string.in_app)
                        )
                    )
                }
            }
        }
    ) {
        if (browsers == null || browserMode == null || selectedBrowser == null) {
            item {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            items(items = browsers!!, key = { it.flatComponentName }) { app ->
                val selected = browserMode!!.matches(BrowserHandler.BrowserMode.SelectedBrowser)
                        && selectedBrowser!!.matches(app.packageName)

                Timber.tag("PreferredBrowserSettingsRoute").d("app=$app")

                RadioButtonRow(
                    selected = selected,
                    onClick = { viewModel.updateSelectedBrowser(app.packageName) },
                    onLongClick = { activity.startPackageInfoActivity(app) }
                ) {
                    BrowserIconTextRow(
                        app = app,
                        selected = selected,
                        showSelectedText = true,
                        alwaysShowPackageName = viewModel.alwaysShowPackageName.value
                    )
                }
            }
        }
    }
}