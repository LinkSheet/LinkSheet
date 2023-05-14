package fe.linksheet.composable.settings.apps.browser

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.junkfood.seal.ui.component.PreferenceSubtitle
import com.tasomaniac.openwith.resolver.BrowserHandler
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.R
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.util.RadioButtonRow
import fe.linksheet.composable.util.Texts
import fe.linksheet.extension.observeAsState
import fe.linksheet.extension.startPackageInfoActivity
import fe.linksheet.ui.theme.HkGroteskFontFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PreferredBrowserSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val lifecycleState = LocalLifecycleOwner.current.lifecycle
        .observeAsState(ignoreFirst = Lifecycle.Event.ON_RESUME)

    LaunchedEffect(Unit) {
        viewModel.loadBrowsers(context)
    }

    LaunchedEffect(lifecycleState.first) {
        if (lifecycleState.first == Lifecycle.Event.ON_RESUME) {
            viewModel.loadBrowsers(context)
        }
    }

    val coroutineScope = rememberCoroutineScope()

    var openDialog by remember { mutableStateOf(false) }
    var save by remember { mutableStateOf(false) }

    LaunchedEffect(openDialog) {
        if (!openDialog && save) {
            viewModel.saveWhitelistedBrowsers()
            save = false
        }
    }

    if (openDialog) {
        AlertDialog(onDismissRequest = { openDialog = false }) {
            Surface(shape = MaterialTheme.shapes.large) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.whitelisted_browsers),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Box {
                        LazyColumn(modifier = Modifier.padding(bottom = 40.dp), content = {
                            viewModel.whitelistedBrowserMap.forEach { (browser, enabled) ->
                                item(key = browser) {
                                    var state by remember { mutableStateOf(enabled) }

                                    ClickableRow(
                                        verticalAlignment = Alignment.CenterVertically,
                                        padding = 2.dp,
                                        onClick = {
                                            viewModel.whitelistedBrowserMap[browser] = !state
                                            state = !state
                                        }
                                    ) {
                                        Checkbox(checked = state, onCheckedChange = {
                                            viewModel.whitelistedBrowserMap[browser] = it
                                            state = it
                                        })

                                        Spacer(modifier = Modifier.width(5.dp))

                                        BrowserIconTextRow(
                                            context = context,
                                            app = browser,
                                            selected = enabled,
                                            showSelectedText = false,
                                            alwaysShowPackageName = viewModel.alwaysShowPackageName
                                        )
                                    }
                                }
                            }
                        })

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .height(40.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                openDialog = false
                                save = true
                            }) {
                                Text(text = stringResource(id = R.string.save))
                            }
                        }
                    }

                }
            }
        }
    }

    SettingsScaffold(R.string.preferred_browser, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 15.dp)
        ) {
            stickyHeader(key = "header") {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    PreferenceSubtitle(
                        text = stringResource(R.string.preferred_browser_explainer),
                        paddingStart = 0.dp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            item(key = "always_ask") {
                RadioButtonRow(
                    selected = viewModel.browserMode == BrowserHandler.BrowserMode.AlwaysAsk,
                    onClick = {
                        viewModel.onBrowserMode(BrowserHandler.BrowserMode.AlwaysAsk)
                    },
                    onLongClick = null
                ) {
                    Texts(
                        headline = R.string.always_ask,
                        subtitle = R.string.always_ask_explainer
                    )
                }
            }

            item(key = "none") {
                RadioButtonRow(
                    selected = viewModel.browserMode == BrowserHandler.BrowserMode.None,
                    onClick = {
                        viewModel.onBrowserMode(BrowserHandler.BrowserMode.None)
                    },
                    onLongClick = null
                ) {
                    Texts(headline = R.string.none, subtitle = R.string.none_explainer)
                }
            }

            item(key = "whitelisted") {
                RadioButtonRow(
                    selected = viewModel.browserMode == BrowserHandler.BrowserMode.Whitelisted,
                    onClick = {
                        viewModel.onBrowserMode(BrowserHandler.BrowserMode.Whitelisted)
                        coroutineScope.launch {
                            viewModel.queryWhitelistedBrowsersAsync(context)
                        }
                        openDialog = true
                    },
                    onLongClick = null
                ) {
                    Texts(
                        headline = R.string.whitelisted,
                        subtitle = R.string.whitelisted_explainer
                    )
                }
            }

            viewModel.browsers.forEach { app ->
                item(key = app.flatComponentName) {
                    val selected =
                        viewModel.browserMode == BrowserHandler.BrowserMode.SelectedBrowser && viewModel.selectedBrowser == app.packageName
                    RadioButtonRow(
                        selected = selected,
                        onClick = {
                            viewModel.onBrowserMode(BrowserHandler.BrowserMode.SelectedBrowser)
                            viewModel.onSelectedBrowser(app.packageName)
                        },
                        onLongClick = {
                            context.startPackageInfoActivity(app)
                        }
                    ) {
                        BrowserIconTextRow(
                            context = context,
                            app = app,
                            selected = selected,
                            showSelectedText = true,
                            alwaysShowPackageName = viewModel.alwaysShowPackageName
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BrowserIconTextRow(
    context: Context,
    app: DisplayActivityInfo,
    selected: Boolean,
    showSelectedText: Boolean,
    alwaysShowPackageName: Boolean
) {
    Image(
        bitmap = app.getBitmap(context),
        contentDescription = app.displayLabel,
        modifier = Modifier.size(32.dp)
    )

    Spacer(modifier = Modifier.width(10.dp))

    Column {
        Text(
            text = app.displayLabel,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold
        )

        if (selected && showSelectedText) {
            Text(
                text = stringResource(id = R.string.selected_browser_explainer),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        if (alwaysShowPackageName) {
            Text(
                text = app.packageName,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

