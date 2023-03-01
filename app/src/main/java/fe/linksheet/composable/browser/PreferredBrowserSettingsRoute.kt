package fe.linksheet.composable.browser

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.junkfood.seal.ui.component.BackButton
import com.junkfood.seal.ui.component.PreferenceSubtitle
import com.tasomaniac.openwith.resolver.BrowserHandler
import fe.linksheet.R
import fe.linksheet.composable.ClickableRow
import fe.linksheet.composable.Searchbar
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.extension.observeAsState
import fe.linksheet.extension.startPackageInfoActivity
import fe.linksheet.ui.theme.HkGroteskFontFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PreferredBrowserSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
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

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.preferred_browser),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }, navigationIcon = {
                    BackButton {
                        onBackPressed()
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, content = {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
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

                item(key = "none") {
                    BrowserRow(
                        selected = viewModel.browserMode == BrowserHandler.BrowserMode.None,
                        onClick = {
                            viewModel.onBrowserMode(BrowserHandler.BrowserMode.None)
                        },
                        onLongClick = null
                    ) {
                        Texts(headline = R.string.none, subtitle = R.string.none_explainer)
                    }
                }

                item(key = "always_ask") {
                    BrowserRow(
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

                viewModel.browsers.forEach { app ->
                    item(key = app.flatComponentName) {
                        val selected =
                            viewModel.browserMode == BrowserHandler.BrowserMode.SelectedBrowser && viewModel.selectedBrowser == app.packageName
                        BrowserRow(
                            selected = selected,
                            onClick = {
                                viewModel.onBrowserMode(BrowserHandler.BrowserMode.SelectedBrowser)
                                viewModel.onSelectedBrowser(app.packageName)
                            },
                            onLongClick = {
                                context.startPackageInfoActivity(app)
                            }
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

                                if (selected) {
                                    Text(
                                        text = stringResource(id = R.string.selected_browser_explainer),
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }

                                if (viewModel.alwaysShowPackageName) {
                                    Text(
                                        text = app.packageName,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        })

}

@Composable
private fun BrowserRow(
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
    content: @Composable () -> Unit
) {
    ClickableRow(
        paddingHorizontal = 0.dp,
        paddingVertical = 5.dp,
        onClick = onClick,
        onLongClick = onLongClick,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            modifier = Modifier
//                .padding(0.dp).border(1.dp, Color.Blue)
        )
        Spacer(modifier = Modifier.width(5.dp))
        content()
    }
}

@Composable
private fun Texts(@StringRes headline: Int, @StringRes subtitle: Int) {
    Column {
        Text(
            text = stringResource(id = headline),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(text = stringResource(id = subtitle))
    }
}