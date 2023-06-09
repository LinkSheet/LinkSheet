package fe.linksheet.composable.settings.browser

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.CheckboxRow
import fe.linksheet.composable.util.mapState
import fe.linksheet.extension.ioState
import fe.linksheet.extension.compose.mapHelper
import fe.linksheet.extension.compose.searchHeader
import fe.linksheet.extension.compose.updateState
import fe.linksheet.extension.compose.updateStateFromResult
import fe.linksheet.module.viewmodel.base.BrowserCommonViewModel
import fe.linksheet.resolver.DisplayActivityInfo

@Composable
fun BrowserCommonPackageSelectorRoute(
    @StringRes headlineId: Int,
    @StringRes subtitleId: Int,
    @StringRes noItemsId: Int,
    navController: NavHostController,
    viewModel: BrowserCommonViewModel,
) {
    val items by viewModel.items.ioState()
    val filter by viewModel.searchFilter.ioState()
    val mapState = remember(items?.size, filter) {
        mapState(items, filter)
    }

    val state = remember {
        mutableStateMapOf<DisplayActivityInfo, Boolean>()
    }

    val backHandler: () -> Unit = {
        viewModel.save(state)
        navController.popBackStack()
    }

    BackHandler(onBack = backHandler)

    SettingsScaffold(
        headlineId = headlineId,
        onBackPressed = backHandler,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 15.dp)
        ) {
            searchHeader(
                subtitleId = subtitleId,
                filter = filter,
                searchFilter = viewModel.searchFilter
            )

            mapHelper(
                noItems = noItemsId,
                notFound = R.string.no_such_app_found,
                mapState = mapState,
                map = items,
                listKey = { it.flatComponentName },
            ) { app, enabled ->
                val enabledState = remember { mutableStateOf(enabled) }
                val update: (Boolean) -> Unit = remember { { state[app] = it } }

                CheckboxRow(
                    checked = enabledState.value,
                    onClick = enabledState.updateState(update),
                    onCheckedChange = enabledState.updateStateFromResult(update)
                ) {
                    BrowserIconTextRow(
                        app = app,
                        selected = enabled,
                        showSelectedText = false,
                        alwaysShowPackageName = viewModel.alwaysShowPackageName.value
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}