package fe.linksheet.composable.settings.apps.link

import android.content.pm.verify.domain.DomainVerificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import fe.linksheet.R
import fe.linksheet.composable.ClickableRow
import fe.linksheet.composable.Searchbar
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.extension.observeAsState
import fe.linksheet.ui.theme.HkGroteskFontFamily
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppsWhichCanOpenLinksSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val manager = remember { context.getSystemService(DomainVerificationManager::class.java) }

    var refreshing by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf("") }

    val fetch: suspend CoroutineScope.(Boolean) -> Unit = { fetchRefresh ->
        if (fetchRefresh) {
            refreshing = true
        }

        viewModel.loadAppsWhichCanHandleLinksAsync(context, manager)
        viewModel.filterWhichAppsCanHandleLinksAsync(filter)

        if (fetchRefresh) {
            delay(100)
            refreshing = false
        }
    }

    val lifecycleState =
        LocalLifecycleOwner.current.lifecycle.observeAsState(Lifecycle.Event.ON_RESUME)

    LaunchedEffect(lifecycleState.first) {
        if (lifecycleState.first == Lifecycle.Event.ON_RESUME) {
            fetch(false)
        }
    }

    LaunchedEffect(Unit) {
        fetch(false)
    }

    val refreshScope = rememberCoroutineScope()

    val state = rememberPullRefreshState(refreshing, onRefresh = {
        refreshScope.launch(block = { fetch(true) })
    })



    SettingsScaffold(R.string.apps_which_can_open_links, onBackPressed = onBackPressed) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(state)
        ) {
            PullRefreshIndicator(
                refreshing = refreshing,
                state = state,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxHeight(),
                contentPadding = PaddingValues(horizontal = 15.dp)
            ) {
                stickyHeader(key = "header") {
                    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                        PreferenceSubtitle(
                            text = stringResource(R.string.apps_which_can_open_links_explainer),
                            paddingStart = 0.dp
                        )

                        Row {
                            FilterChip(
                                selected = viewModel.whichAppsCanHandleLinksEnabled,
                                onClick = {
                                    viewModel.onWhichAppsCanHandleLinksEnabled(true)
                                    refreshScope.launch { fetch(true) }
                                },
                                label = {
                                    Text(text = stringResource(id = R.string.enabled))
                                },
                                trailingIcon = {
                                    Image(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = stringResource(
                                            id = R.string.enabled
                                        ),
                                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                                    )
                                }
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            FilterChip(
                                selected = !viewModel.whichAppsCanHandleLinksEnabled,
                                onClick = {
                                    viewModel.onWhichAppsCanHandleLinksEnabled(false)
                                    refreshScope.launch { fetch(true) }
                                },
                                label = {
                                    Text(text = stringResource(id = R.string.disabled))
                                },
                                trailingIcon = {
                                    Image(
                                        imageVector = Icons.Default.VisibilityOff,
                                        contentDescription = stringResource(
                                            id = R.string.disabled
                                        ),
                                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Searchbar(filter = filter, onValueChange = {
                            filter = it
                            refreshScope.launch {
                                viewModel.filterWhichAppsCanHandleLinksAsync(
                                    it
                                )
                            }
                        }, onClearClick = {
                            filter = ""
                            refreshScope.launch {
                                viewModel.filterWhichAppsCanHandleLinksAsync(
                                    filter
                                )
                            }
                        })

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                if (viewModel.whichAppsCanHandleLinksFiltered.isNotEmpty() && !refreshing) {
                    items(
                        viewModel.whichAppsCanHandleLinksFiltered,
                        key = { it.flatComponentName }
                    ) { info ->
                        ClickableRow(
                            padding = 5.dp,
                            onClick = {
                                viewModel.openOpenByDefaultSettings(
                                    context,
                                    info.packageName
                                )
                            },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                bitmap = info.getBitmap(context),
                                contentDescription = info.displayLabel,
                                modifier = Modifier.size(42.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = info.displayLabel, fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (viewModel.alwaysShowPackageName) {
                                    Text(
                                        text = info.packageName,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .fillParentMaxHeight(0.4f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (viewModel.whichAppsCanHandleLinksLoading) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            } else {
                                Text(
                                    text = stringResource(id = R.string.no_such_app_found),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}