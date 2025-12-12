package app.linksheet.feature.app.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.feature.app.R
import app.linksheet.feature.app.applist.AppListCommon
import app.linksheet.feature.app.core.IAppInfo
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContent
import fe.composekit.component.appbar.SearchTopAppBar


@Composable
fun <T : IAppInfo> AppFilterSearchTopAppBar(
    appListCommon: AppListCommon<T>,
    titleContent: TextContent,
    onBackPressed: () -> Unit,
    actions: @Composable (RowScope.() -> Unit) = {},
) {
    val searchFilter by appListCommon.searchQuery.collectAsStateWithLifecycle()
    SearchTopAppBar(
        titleContent = titleContent,
        placeholderContent = textContent(R.string.settings__title_filter_apps),
        query = searchFilter,
        onQueryChange = appListCommon::search,
        onBackPressed = onBackPressed,
        actions = actions
    )
}
