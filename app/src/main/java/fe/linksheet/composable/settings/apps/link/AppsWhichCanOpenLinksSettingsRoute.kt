package fe.linksheet.composable.settings.apps.link

import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.junkfood.seal.ui.component.PreferenceSubtitle
import dev.zwander.shared.ShizukuUtil
import dev.zwander.shared.ShizukuUtil.rememberHasShizukuPermissionAsState
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.FilterChipValue
import fe.linksheet.composable.util.FilterChips
import fe.linksheet.composable.util.LaunchedEffectOnFirstAndResume
import fe.linksheet.composable.util.Searchbar
import fe.linksheet.composable.util.listState
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.extension.ioState
import fe.linksheet.module.viewmodel.AppsWhichCanOpenLinksViewModel
import fe.linksheet.resolver.DisplayActivityInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import rikka.shizuku.Shizuku
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@OptIn(
    ExperimentalFoundationApi::class
)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppsWhichCanOpenLinksSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: AppsWhichCanOpenLinksViewModel = koinViewModel(),
    enableBackButton: Boolean = true,
) {
    val activity = LocalContext.currentActivity()

    val apps by viewModel.appsFiltered.ioState()
    val filter by viewModel.searchFilter.ioState()
    val linkHandlingAllowed by viewModel.linkHandlingAllowed.ioState()

    val listState = remember(apps?.size, filter, linkHandlingAllowed) {
        listState(apps, filter)
    }

    var shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(activity)) }
    var shizukuRunning by remember { mutableStateOf(ShizukuUtil.isShizukuRunning()) }

    var refreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val shizukuPermission by rememberHasShizukuPermissionAsState()

    val shizukuMode = shizukuInstalled && shizukuRunning && shizukuPermission

    val fetch: suspend (Boolean) -> Unit = { fetchRefresh ->
        if (fetchRefresh) {
            refreshing = true
        }

        viewModel.refreshing.value = true

        if (fetchRefresh) {
            delay(100)
            refreshing = false
        }
    }

    LaunchedEffectOnFirstAndResume { fetch(false) }

    val fetchInScope: () -> Unit = {
        scope.launch { fetch(true) }
    }
    val state = rememberPullRefreshState(refreshing, onRefresh = fetchInScope)

    fun postCommand(packageName: String) {
        viewModel.app.postShizukuCommand {
            disableLinkHandling(packageName, !linkHandlingAllowed)
        }

        fetchInScope()
    }

    fun openDefaultSettings(info: DisplayActivityInfo) {
        activity.startActivityWithConfirmation(viewModel.makeOpenByDefaultSettingsIntent(info))
    }

    SettingsScaffold(R.string.apps_which_can_open_links, enableBackButton = enableBackButton, onBackPressed = onBackPressed) { padding ->
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
                    .fillMaxHeight(), contentPadding = PaddingValues(horizontal = 15.dp)
            ) {
                stickyHeader(key = "header") {
                    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                        PreferenceSubtitle(
                            text = stringResource(
                                if (shizukuMode && viewModel.featureFlagShizuku.value) R.string.apps_which_can_open_links_shizuku_explainer
                                else R.string.apps_which_can_open_links_explainer
                            ),
                            paddingStart = 0.dp
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilterChips(
                                currentState = linkHandlingAllowed,
                                onClick = {
                                    viewModel.linkHandlingAllowed.value = it
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

                            if (shizukuMode && viewModel.featureFlagShizuku.value) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { postCommand("all") }) {
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
                    listKey = { it.flatComponentName },
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
                            bitmap = info.iconBitmap,
                            contentDescription = info.label,
                            modifier = Modifier.size(42.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = info.label, fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (viewModel.alwaysShowPackageName.value) {
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


