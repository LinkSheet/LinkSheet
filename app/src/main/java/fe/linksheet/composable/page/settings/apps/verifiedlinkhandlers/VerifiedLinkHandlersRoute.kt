package fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.compose.extension.collectOnIO
import app.linksheet.feature.app.ui.AppListPage
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.core.AndroidVersion
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.composekit.route.Route
import fe.linksheet.R
import fe.linksheet.composable.dialog.DomainVerificationDialogData
import fe.linksheet.composable.dialog.rememberDomainVerificationAppInfoDialog
import fe.linksheet.extension.android.tryStartActivity
import fe.linksheet.extension.compose.ObserveStateChange
import fe.linksheet.module.viewmodel.VerifiedLinkHandlersViewModel
import fe.linksheet.navigation.VlhAppRoute
import org.koin.androidx.compose.koinViewModel

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun VerifiedLinkHandlersRoute(
    navigateNew: (Route) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: VerifiedLinkHandlersViewModel = koinViewModel(),
) {
    val activity = LocalActivity.current

    LocalLifecycleOwner.current.lifecycle.ObserveStateChange(invokeOnCall = true) {
//        viewModel.refresh()
    }

    val preferredApps by viewModel.preferredApps.collectOnIO(emptyMap())

    val dialogState = rememberDomainVerificationAppInfoDialog(
        onClose = { (info, hostStates) ->
            viewModel.updateHostState(info, hostStates)
        }
    )

    val lazyListState = rememberLazyListState()

    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val newVlh by viewModel.newVlh.collectAsStateWithLifecycle()

    AppListPage(
        titleContent = textContent(R.string.apps_which_can_open_links),
        appListModel = viewModel.appListModel,
        lazyListState = lazyListState,
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = { showBottomSheet = true }) {
                Icon(
                    imageVector = Icons.Rounded.FilterList,
                    contentDescription = null,
                )
            }
        },
        additionalContent = {
            var lastItemIndex by rememberSaveable { mutableIntStateOf(0) }
            if (showBottomSheet) {
                val sortState by viewModel.appListModel.sortState.collectAsStateWithLifecycle()
                val filterState by viewModel.appListModel.filterState.collectAsStateWithLifecycle()
                FilterSortSheet(
                    sortState = sortState,
                    filterState = filterState,
                    onDismiss = { sortByState, filterState ->
                        lastItemIndex = lazyListState.firstVisibleItemIndex
                        viewModel.appListModel.updateState(sortByState, filterState)
                        showBottomSheet = false
                    }
                )
            }
        }
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
                    dialogState.open(
                        DomainVerificationDialogData(
                            item,
                            preferredHosts
                        )
                    )
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

