package app.linksheet.feature.app.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.compose.util.listState
import app.linksheet.feature.app.applist.AppListModel
import app.linksheet.feature.app.core.IAppInfo
import fe.android.compose.text.TextContent
import fe.composekit.component.CommonDefaults
import fe.composekit.component.list.column.SaneLazyColumnLayout
import fe.composekit.component.page.SaneSettingsScaffold
import my.nanihadesuka.compose.InternalLazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun <T : IAppInfo> AppListPage(
    titleContent: TextContent,
    appListModel: AppListModel<T>,
    lazyListState:  LazyListState = rememberLazyListState(),
    refreshState: PullToRefreshState = rememberPullToRefreshState(),
    onBackPressed: () -> Unit,
    actions: @Composable (RowScope.() -> Unit) = {},
    additionalContent: @Composable () -> Unit = {},
    listItem: @Composable LazyItemScope.(T, PaddingValues, Shape) -> Unit
) {
    val appListState by appListModel.appsFiltered.collectAsStateWithLifecycle()
    val searchFilter by appListModel.searchQuery.collectAsStateWithLifecycle()

    val listState = remember(appListState, searchFilter) {
        listState(list = appListState?.apps, filter = searchFilter)
    }
    val isRefreshing = appListState?.isLoading ?: true

    SaneSettingsScaffold(
        topBar = {
            AppFilterSearchTopAppBar(
                appListModel = appListModel,
                titleContent = titleContent,
                onBackPressed = onBackPressed,
                actions = actions
            )
        }
    ) { padding ->
        additionalContent()

        PullToRefreshBox(
            modifier = Modifier.padding(padding),
            isRefreshing = isRefreshing,
            onRefresh = appListModel::refresh,
            state = refreshState,
            indicator = {
                @OptIn(ExperimentalMaterial3ExpressiveApi::class)
                PullToRefreshDefaults.LoadingIndicator(
                    state = refreshState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        ) {
            SaneLazyColumnLayout(
                state = lazyListState,
                padding = CommonDefaults.EmptyPadding
            ) {
                item(key = "0") {
                    // Works around odd re-order scroll behavior: https://issuetracker.google.com/issues/234223556
                }
                appList(
                    listState = listState,
                    list = appListState?.apps,
                    listKey = IAppInfo::uniqueKey,
                    content = listItem
                )
            }

            InternalLazyColumnScrollbar(
                modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                state = lazyListState,
                settings = ScrollbarSettings.Default.copy(
//                        alwaysShowScrollbar = true,
                    thumbSelectedColor = MaterialTheme.colorScheme.primary,
                    thumbUnselectedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
