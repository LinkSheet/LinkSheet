package fe.linksheet.composable.page.settings.browser.mode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.R
import fe.linksheet.composable.page.settings.browser.BrowserCommonPackageSelectorData
import fe.linksheet.composable.page.settings.browser.BrowserCommonRadioButtonRowData
import fe.linksheet.composable.page.settings.browser.BrowserCommonScaffold
import fe.linksheet.composable.page.settings.browser.BrowserIconTextRow
import fe.linksheet.composable.util.FilterChipValue
import fe.linksheet.composable.util.FilterChips
import fe.linksheet.composable.util.RadioButtonRow
import fe.linksheet.composable.util.SettingEnabledCardColumn
import fe.linksheet.extension.android.startPackageInfoActivity
import fe.linksheet.extension.compose.currentActivity
import app.linksheet.compose.extension.collectOnIO
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.module.viewmodel.PreferredBrowserViewModel
import fe.linksheet.navigation.whitelistedBrowsersSettingsRoute
import org.koin.androidx.compose.koinViewModel

@Composable
fun PreferredBrowserSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: PreferredBrowserViewModel = koinViewModel(),
) {
    val activity = LocalContext.currentActivity()

    val browsers by viewModel.browsers.collectOnIO(null)
    val type by viewModel.type.collectOnIO()
    val browserModePreference by viewModel.browserModeState.collectOnIO(null)
    val selectedBrowserPreference by viewModel.selectedBrowserState.collectOnIO(null)

    val browserMode = browserModePreference?.collectAsStateWithLifecycle()
    val selectedBrowser = selectedBrowserPreference?.collectAsStateWithLifecycle()

    val rows = remember {
        listOf(
            BrowserCommonRadioButtonRowData(
                R.string.always_ask,
                R.string.always_ask_explainer
            ),
            BrowserCommonRadioButtonRowData(
                R.string.none,
                R.string.none_explainer
            )
        )
    }

    BrowserCommonScaffold(
        navController = navController,
        headline = R.string.browser_mode,
        explainer = R.string.browser_mode_subtitle,
        onBackPressed = onBackPressed,
        values = listOf(
            BrowserMode.AlwaysAsk,
            BrowserMode.None,
        ),
        state = browserModePreference,
        rowKey = { it.value },
        rows = rows,
        header = {
            val unifiedPreferredBrowser by viewModel.unifiedPreferredBrowser.collectAsStateWithLifecycle()

            SettingEnabledCardColumn(
                checked = unifiedPreferredBrowser,
                onChange = { viewModel.unifiedPreferredBrowser(it) },
                headline = stringResource(id = R.string.use_unified_preferred_browser),
                subtitle = stringResource(id = R.string.use_unified_preferred_browser_explainer)
            )
            Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                if (!unifiedPreferredBrowser) {
                    FilterChips(
                        currentState = type,
                        onClick = { viewModel.type.value = it },
                        values = listOf(
                            FilterChipValue(
                                PreferredBrowserViewModel.BrowserType.Normal,
                                R.string.normal
                            ),
                            FilterChipValue(
                                PreferredBrowserViewModel.BrowserType.InApp,
                                R.string.in_app
                            )
                        )
                    )
                }
            }
        },
        browsers = browsers,
        selectorData = BrowserCommonPackageSelectorData(
            R.string.whitelisted,
            R.string.whitelisted_explainer,
            BrowserMode.Whitelisted,
            whitelistedBrowsersSettingsRoute
        )
    ) { browserState ->
        if (browserState == null || browserModePreference == null || selectedBrowserPreference == null) {
            item(key = "loader") {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            items(
                items = browserState,
                key = { it.flatComponentName },
                contentType = { it.flatComponentName }
            ) { app ->
                val selected = browserMode?.value == BrowserMode.SelectedBrowser && selectedBrowser?.value == app.packageName

                RadioButtonRow(
                    selected = selected,
                    onClick = { viewModel.updateSelectedBrowser(app.packageName) },
                    onLongClick = { activity.startPackageInfoActivity(app) }
                ) {
                    val alwaysShowPackageName by viewModel.alwaysShowPackageName.collectAsStateWithLifecycle()

                    BrowserIconTextRow(
                        app = app,
                        selected = selected,
                        showSelectedText = true,
                        alwaysShowPackageName = alwaysShowPackageName
                    )
                }
            }
        }
    }
}
