package fe.linksheet.composable.settings.browser

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.CheckboxRow
import fe.linksheet.composable.util.mapState
import fe.linksheet.extension.compose.mapHelper
import fe.linksheet.extension.compose.searchHeader
import fe.linksheet.extension.kotlin.collectOnIO
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
    val items by viewModel.filteredItems.collectOnIO()
    val filter by viewModel.filter.collectOnIO()
    val mapState = remember(items?.size, filter) {
        mapState(items, filter)
    }

    val newState = remember {
        mutableStateMapOf<DisplayActivityInfo, Boolean>()
    }

    val backHandler: () -> Unit = {
        viewModel.save(newState)
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
                searchFilter = viewModel.filter
            )

            mapHelper(
                noItems = noItemsId,
                notFound = R.string.no_such_app_found,
                mapState = mapState,
                map = items,
                listKey = { it.flatComponentName },
            ) { app, storedState ->
                CheckboxRow(
                    checked = newState[app] ?: storedState,
                    onCheckedChange = { newState[app] = it }
                ) {
                    BrowserIconTextRow(
                        app = app,
                        selected = storedState,
                        showSelectedText = false,
                        alwaysShowPackageName = viewModel.alwaysShowPackageName()
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}
