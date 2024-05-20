package fe.linksheet.composable.settings.apps.link

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.zwander.shared.ShizukuUtil
import dev.zwander.shared.ShizukuUtil.rememberHasShizukuPermissionAsState
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.*
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.extension.compose.ObserveStateChange
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.viewmodel.AppsWhichCanOpenLinksViewModel
import fe.linksheet.module.viewmodel.PretendToBeAppSettingsViewModel
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.ui.LocalActivity
import org.koin.androidx.compose.koinViewModel


private const val allPackages = "all"

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class
)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppsWhichCanOpenLinksSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: AppsWhichCanOpenLinksViewModel = koinViewModel(),
) {
    val activity = LocalActivity.current

    val apps by viewModel.appsFiltered.collectOnIO()
    val filter by viewModel.searchFilter.collectOnIO()
    val linkHandlingAllowed by viewModel.filterDisabledOnly.collectOnIO(true)
    val lastEmitted by viewModel.lastEmitted.collectOnIO()

    val listState = remember(apps?.size, filter, linkHandlingAllowed) {
        listState(apps, filter)
    }

    val shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(activity)) }
    val shizukuRunning by remember { mutableStateOf(ShizukuUtil.isShizukuRunning()) }

    val shizukuPermission by rememberHasShizukuPermissionAsState()

    val shizukuMode = shizukuInstalled && shizukuRunning && shizukuPermission
    val state = rememberPullToRefreshState()

    LocalLifecycleOwner.current.lifecycle.ObserveStateChange(invokeOnCall = true) {
        viewModel.emitLatest()
    }

    // TODO: Refactor refresh indicator
    LaunchedEffect(lastEmitted) {
//        state.endRefresh()
    }

    fun postCommand(packageName: String) {
//        state.startRefresh()
        viewModel.postShizukuCommand(if (linkHandlingAllowed) 0 else 500) {
            val newState = !linkHandlingAllowed
            val result = setDomainState(packageName, "all", newState)
            if (packageName == allPackages) {
                // TODO: Revert previous state instead of always setting to !newState
                setDomainState(PretendToBeAppSettingsViewModel.linksheetCompatPackage, "all", !newState)
            }

            result
        }
    }

    fun openDefaultSettings(info: DisplayActivityInfo) {
        activity.startActivityWithConfirmation(viewModel.makeOpenByDefaultSettingsIntent(info))
    }

    fun openDefaultSettings(info: Any) {
//        activity.startActivityWithConfirmation(viewModel.makeOpenByDefaultSettingsIntent(info))
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    SettingsScaffold(R.string.apps_which_can_open_links, onBackPressed = onBackPressed) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
//                .nestedScroll(state.nestedScrollConnection)
        ) {
//            PullToRefreshContainer(state = state, modifier = Modifier.align(Alignment.TopCenter))

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxHeight(), contentPadding = PaddingValues(horizontal = 15.dp)
            ) {
                stickyHeader(key = "header") {
                    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                        PreferenceSubtitle(
                            text = stringResource(
                                if (shizukuMode) R.string.apps_which_can_open_links_shizuku_explainer
                                else R.string.apps_which_can_open_links_explainer
                            ),
                            paddingHorizontal = 0.dp
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            IconButton(onClick = { /*TODO*/ }) {
//                                Icon(
//                                    imageVector = Icons.Default.Settings,
//                                    contentDescription = stringResource(id = R.string.verified_link_handlers_open_settings_page),
//                                    tint = MaterialTheme.colorScheme.primary
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.width(5.dp))

                            FilterChips(
                                currentState = linkHandlingAllowed,
                                onClick = {
                                    viewModel.filterDisabledOnly.value = it
//                                    coroutineScope.launch {
//                                        viewModel.pagerState.scrollToPage(if (it) 0 else 1)
//                                    }
                                },
                                values = listOf(
                                    FilterChipValue(
                                        true,
                                        R.string.enabled,
                                        Icons.Default.Visibility
                                    ),
                                    FilterChipValue(
                                        false,
                                        R.string.disabled,
                                        Icons.Default.VisibilityOff
                                    )
                                )
                            )

                            if (shizukuMode) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { postCommand(allPackages) }) {
                                        Text(text = stringResource(id = if (linkHandlingAllowed) R.string.disable_all else R.string.enable_all))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Searchbar(filter = filter, onFilterChanged = {
                            viewModel.searchFilter.value = it
                        })

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                listHelper(
                    noItems = R.string.no_apps_which_can_handle_links_found,
                    notFound = R.string.no_such_app_found,
                    listState = listState,
                    list = apps,
                    listKey = { it.applicationInfo.packageName },
                ) { info ->
                    ClickableRow(
                        onClick = {
                            if (shizukuMode) postCommand(info.packageName)
                            else openDefaultSettings(info)
                        },
                        onLongClick = {
                            if (shizukuMode) openDefaultSettings(info)
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            bitmap = info.loadIcon(context),
                            contentDescription = info.label,
                            modifier = Modifier.size(42.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = info.label, fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (viewModel.alwaysShowPackageName()) {
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
            }
        }
    }
}


