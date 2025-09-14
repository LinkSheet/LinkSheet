package fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zwander.shared.ShizukuUtil
import dev.zwander.shared.ShizukuUtil.rememberHasShizukuPermissionAsState
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.appbar.SearchTopAppBar
import fe.composekit.component.list.column.SaneLazyColumnLayout
import fe.composekit.component.page.SaneSettingsScaffold
import fe.composekit.core.AndroidVersion
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.composekit.route.Route
import fe.linksheet.R
import fe.linksheet.extension.compose.ObserveStateChange
import app.linksheet.compose.extension.listHelper
import app.linksheet.compose.util.listState
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.viewmodel.PretendToBeAppSettingsViewModel
import fe.linksheet.module.viewmodel.VerifiedLinkHandlersViewModel
import fe.linksheet.navigation.VlhAppRoute
import fe.linksheet.util.extension.android.tryStartActivity
import my.nanihadesuka.compose.InternalLazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings
import org.koin.androidx.compose.koinViewModel

private const val allPackages = "all"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VerifiedLinkHandlersRoute(
    navigateNew: (Route) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: VerifiedLinkHandlersViewModel = koinViewModel(),
) {
    val activity = LocalActivity.current

    val linkHandlingAllowed by viewModel.filterDisabledOnly.collectOnIO(true)

    val lastEmitted by viewModel.lastEmitted.collectOnIO()

    val context = LocalContext.current
    val shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(context)) }
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

    val preferredApps by viewModel.preferredApps.collectOnIO(emptyMap())

    val items by viewModel.appsFiltered.collectOnIO()
    val filter by viewModel.searchQuery.collectOnIO()

    val listState = remember(items?.size, filter, linkHandlingAllowed) {
        listState(items, filter)
    }

    val dialogState = rememberAppHostDialog(
        onClose = { (info, hostStates) ->
            viewModel.updateHostState(info, hostStates)
        }
    )

    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

    SaneSettingsScaffold(
        topBar = {
            SearchTopAppBar(
                titleContent = textContent(R.string.apps_which_can_open_links),
                placeholderContent = textContent(R.string.settings__title_filter_apps),
                query = filter,
                onQueryChange = viewModel::search,
                onBackPressed = onBackPressed,
                actions = {
                    IconButton(onClick = {
                        showBottomSheet = true
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.FilterList,
                            contentDescription = null,
                        )
                    }
                }
            )
        }
    ) { padding ->
//        if (items == null) {
//            Box(modifier = Modifier.padding(padding)) {
//                LinearWavyProgressIndicator(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 4.dp)
//                        .align(Alignment.Center)
//                )
//            }
//        }
//        FilterState(VlhStateModeFilter.ShowAll, VlhTypeFilter.All, true)
        val state = rememberLazyListState()
        var lastItemIndex by rememberSaveable { mutableIntStateOf(0) }
        if (showBottomSheet) {
            val sortState by viewModel.sortState.collectAsStateWithLifecycle()
            val filterState by viewModel.filterState.collectAsStateWithLifecycle()
            FilterSortSheet(
                sortState = sortState,
                filterState = filterState,
                onDismiss = { sortByState, filterState ->
                    lastItemIndex = state.firstVisibleItemIndex
                    viewModel.sortState.value = sortByState
                    viewModel.filterState.value = filterState
                    showBottomSheet = false
                }
            )
        }

        Column(modifier = Modifier.padding(padding)) {
            val newVlh by viewModel.newVlh.collectAsStateWithLifecycle()
            Box(modifier = Modifier) {
                SaneLazyColumnLayout(
                    state = state,
                    padding = PaddingValues()
                ) {
                    item(key = "0") {
                        // Works around odd re-order scroll behavior: https://issuetracker.google.com/issues/234223556
                    }
                    listHelper(
                        noItems = R.string.no_apps_found,
                        notFound = R.string.no_such_app_found,
                        listState = listState,
                        list = items,
                        listKey = { it.packageName }
                    ) { item, padding, shape ->
                        val preferredHosts = remember(preferredApps, item) {
                            preferredApps[item.packageName]?.toSet() ?: emptySet()
                        }
                        VerifiedAppListItem(
                            item = item,
                            padding = padding,
                            shape = shape,
                            preferredHosts = preferredHosts.size,
                            onClick = {
                                if (newVlh) {
                                    navigateNew(VlhAppRoute(item.packageName))
                                } else {
                                    dialogState.open(AppHostDialogData(item, preferredHosts))
                                }
                            },
                            onOtherClick = AndroidVersion.atLeastApi(Build.VERSION_CODES.S) {
                                {
                                    activity?.tryStartActivity(
                                        viewModel.makeOpenByDefaultSettingsIntent(item.packageName)
                                    )
                                }
                            }
                        )
                    }
                }
                InternalLazyColumnScrollbar(
                    modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                    state = state,
                    settings = ScrollbarSettings.Default.copy(
//                        alwaysShowScrollbar = true,
                        thumbSelectedColor = MaterialTheme.colorScheme.primary,
                        thumbUnselectedColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
//            LazyColumnScrollbar(
//                modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars),
//                state = state,
//                settings =
//            ) {
//                Box(modifier = Modifier) {
//
//                }
//            }
        }
    }
}

