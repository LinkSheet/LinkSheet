package fe.linksheet.composable.page.settings.apps

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.zwander.shared.ShizukuUtil
import dev.zwander.shared.ShizukuUtil.rememberHasShizukuPermissionAsState
import fe.android.compose.content.rememberOptionalContent
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.version.AndroidVersion
import fe.composekit.component.appbar.SearchTopAppBar
import fe.composekit.component.list.column.SaneLazyColumnDefaults
import fe.composekit.component.list.column.SaneLazyColumnLayout
import fe.composekit.component.page.SaneSettingsScaffold
import fe.linksheet.R
import fe.linksheet.composable.ui.LocalActivity
import fe.linksheet.composable.util.listState
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.extension.compose.ObserveStateChange
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.viewmodel.FilterMode
import fe.linksheet.module.viewmodel.PretendToBeAppSettingsViewModel
import fe.linksheet.module.viewmodel.VerifiedLinkHandlersViewModel
import org.koin.androidx.compose.koinViewModel

private const val allPackages = "all"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VerifiedLinkHandlersRoute(
    onBackPressed: () -> Unit,
    viewModel: VerifiedLinkHandlersViewModel = koinViewModel(),
) {
    val activity = LocalActivity.current

    val linkHandlingAllowed by viewModel.filterDisabledOnly.collectOnIO(true)

    val userApps by viewModel.userAppFilter.collectOnIO()
    val lastEmitted by viewModel.lastEmitted.collectOnIO()


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

    val context = LocalContext.current

    val preferredApps by viewModel.preferredApps.collectOnIO(emptyMap())

    val items by viewModel.appsFiltered.collectOnIO()
    val filter by viewModel.searchQuery.collectOnIO()
    val filterMode by viewModel.filterMode.collectOnIO()

    val listState = remember(items.size, filter, linkHandlingAllowed) {
        listState(items, filter)
    }

    val dialogState = rememberAppHostDialog(
        onClose = { (info, hostStates) ->
            viewModel.updateHostState(info, hostStates)
        }
    )

    SaneSettingsScaffold(
        topBar = {
            SearchTopAppBar(
                titleContent = textContent(R.string.apps_which_can_open_links),
                placeholderContent = textContent(R.string.settings__title_filter_apps),
                query = filter,
                onQueryChange = viewModel::search,
                onBackPressed = onBackPressed
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SaneLazyColumnDefaults.HorizontalSpacing),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StateFilter(
                    selection = filterMode,
                    onSelected = {
                        viewModel.filterMode.value = it
                    }
                )

                FilterChip(
                    selected = userApps,
                    onClick = {
                        viewModel.userAppFilter.value = !userApps
                    },
                    label = {
                        Text(text = stringResource(id = R.string.settings_verified_link_handlers__text_user_apps))
                    },
                    leadingIcon = rememberOptionalContent(userApps) {
                        Icon(
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                            imageVector = Icons.Filled.Done,
                            contentDescription = null,
                        )
                    }
                )
            }

            SaneLazyColumnLayout(padding = PaddingValues()) {
                listHelper(
                    noItems = R.string.no_apps_found,
                    notFound = R.string.no_such_app_found,
                    listState = listState,
                    list = items,
                    listKey = { it.packageName }
                ) { item, padding, shape ->
                    VerifiedAppListItem(
                        item = item,
                        padding = padding,
                        shape = shape,
                        onClick = {
                            val preferredHosts = preferredApps[item.packageName]?.toSet() ?: emptySet()
                            dialogState.open(AppHostDialogData(item, preferredHosts))
                        },
                        onOtherClick = AndroidVersion.atLeastApi(Build.VERSION_CODES.S) {
                            {
                                activity.startActivityWithConfirmation(
                                    viewModel.makeOpenByDefaultSettingsIntent(item.packageName)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StateFilter(
    selection: FilterMode,
    onSelected: (FilterMode) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "Arrow rotation")


    // From androidx.compose.material3.tokens#FilterChipTokens
    // val UnselectedLabelTextColor = ColorSchemeKeyTokens.OnSurfaceVariant
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box {
        FilterChip(
            selected = selection != FilterMode.ShowAll,
            onClick = { expanded = !expanded },
            colors = FilterChipDefaults.filterChipColors(iconColor = unselectedColor),
            label = {
                Text(
                    text = stringResource(
                        id = selection.shortStringRes
                    )
                )
            },
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                    imageVector = selection.icon,
                    contentDescription = null,
                )
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.rotate(rotation),
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = null
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for (mode in FilterMode.Modes) {
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onSelected(mode)
                    },
                    text = {
                        Text(text = stringResource(id = mode.stringRes))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = mode.icon,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}
