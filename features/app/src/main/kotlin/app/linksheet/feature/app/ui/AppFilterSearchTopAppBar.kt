package app.linksheet.feature.app.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.feature.app.R
import app.linksheet.feature.app.applist.AppListModel
import app.linksheet.feature.app.core.IAppInfo
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContent
import fe.composekit.component.appbar.SearchTopAppBar


@Composable
fun <T : IAppInfo> AppFilterSearchTopAppBar(
    appListModel: AppListModel<T>,
    titleContent: TextContent,
    onBackPressed: () -> Unit,
    actions: @Composable (RowScope.() -> Unit) = {},
) {
    val searchFilter by appListModel.searchQuery.collectAsStateWithLifecycle()
    SearchTopAppBar(
        titleContent = titleContent,
        placeholderContent = textContent(R.string.settings__title_filter_apps),
        query = searchFilter,
        onQueryChange = appListModel::search,
        onBackPressed = onBackPressed,
        actions = actions
    )
}
