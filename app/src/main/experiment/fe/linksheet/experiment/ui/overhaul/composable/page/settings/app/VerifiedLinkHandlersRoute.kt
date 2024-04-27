package fe.linksheet.experiment.ui.overhaul.composable.page.settings.app

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.zwander.shared.ShizukuUtil
import dev.zwander.shared.ShizukuUtil.rememberHasShizukuPermissionAsState
import fe.linksheet.R
import fe.linksheet.composable.util.*
import fe.linksheet.experiment.ui.overhaul.composable.component.appbar.SearchTopAppBar
import fe.linksheet.experiment.ui.overhaul.composable.component.icon.AppIconImage
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ContentPosition
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.CheckboxListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneSettingsScaffold
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.SaneLazyColumnPageLayout
import fe.linksheet.experiment.ui.overhaul.composable.component.util.ComposableTextContent
import fe.linksheet.experiment.ui.overhaul.composable.component.util.Resource.Companion.textContent
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.extension.compose.ObserveStateChange
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.viewmodel.AppsWhichCanOpenLinksViewModel
import fe.linksheet.module.viewmodel.PretendToBeAppSettingsViewModel
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.ui.LocalActivity
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


private const val allPackages = "all"

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class
)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun VerifiedLinkHandlersRoute(
    onBackPressed: () -> Unit,
    viewModel: AppsWhichCanOpenLinksViewModel = koinViewModel(),
) {
    val activity = LocalActivity.current


    val linkHandlingAllowed by viewModel.linkHandlingAllowed.collectOnIO(true)
    val lastEmitted by viewModel.lastEmitted.collectOnIO()


    val shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(activity)) }
    val shizukuRunning by remember { mutableStateOf(ShizukuUtil.isShizukuRunning()) }

    val shizukuPermission by rememberHasShizukuPermissionAsState()

    val shizukuMode = shizukuInstalled && shizukuRunning && shizukuPermission
    val state = rememberPullToRefreshState()

    LocalLifecycleOwner.current.lifecycle.ObserveStateChange(invokeOnCall = true) {
        viewModel.emitLatest()
    }

    LaunchedEffect(lastEmitted) { state.endRefresh() }

    fun postCommand(packageName: String) {
        state.startRefresh()
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

    val context = LocalContext.current

    val items by viewModel.appsFiltered.collectOnIO()
    val filter by viewModel.searchFilter.collectOnIO()

    val listState = remember(items?.size, filter, linkHandlingAllowed) {
        listState(items, filter)
    }

//    var tabState by remember { mutableIntStateOf(0) }
    val options = remember {
        listOf(
            R.string.enabled to Icons.Outlined.Visibility,
            R.string.disabled to Icons.Outlined.VisibilityOff
        )
    }

    val coroutineScope = rememberCoroutineScope()

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
            SecondaryTabRow(selectedTabIndex = viewModel.pagerState.currentPage) {
                options.forEachIndexed { index, (stringRes, icon) ->
                    LeadingIconTab(
                        selected = viewModel.pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { viewModel.pagerState.animateScrollToPage(page = index) } },
                        text = {
                            Text(text = stringResource(id = stringRes))
                        },
                        icon = { Icon(imageVector = icon, contentDescription = "Favorite") }
                    )
                }
            }

            HorizontalPager(state = viewModel.pagerState) {
                SaneLazyColumnPageLayout(padding = PaddingValues()) {
                    listHelper(
                        noItems = R.string.no_apps_found,
                        notFound = R.string.no_such_app_found,
                        listState = listState,
                        list = items,
                        listKey = { it.packageName }
                    ) { item, padding, shape ->
                        ClickableShapeListItem(
                            padding = padding,
                            shape = shape,
                            position = ContentPosition.Trailing,
                            onClick = {
                                if (shizukuMode) postCommand(item.packageName)
                                else openDefaultSettings(item)
                            },
                            headlineContent = ComposableTextContent.content {
                                Text(text = item.label, overflow = TextOverflow.Ellipsis, maxLines = 1)
                            },
                            supportingContent = ComposableTextContent.content {
                                Text(text = item.packageName, overflow = TextOverflow.Ellipsis, maxLines = 1)
                            },
                            otherContent = {
                                AppIconImage(
                                    bitmap = item.getIcon(context),
//                            bitmap = item.loadIcon(context),
                                    label = item.label
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
