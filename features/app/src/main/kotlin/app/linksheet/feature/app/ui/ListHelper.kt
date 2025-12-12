package app.linksheet.feature.app.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import app.linksheet.compose.extension.listHelper
import app.linksheet.compose.util.ListState
import app.linksheet.feature.app.R
import app.linksheet.feature.app.core.IAppInfo
import fe.composekit.layout.column.SaneLazyListScope


fun <T : IAppInfo> SaneLazyListScope.appList(
    listState: ListState,
    list: List<T>?,
    listKey: (T) -> Any,
    content: @Composable LazyItemScope.(T, PaddingValues, Shape) -> Unit,
){
    listHelper(
        noItems = R.string.no_apps_found,
        notFound = R.string.no_such_app_found,
        listState = listState,
        list = list,
        listKey = listKey,
        content = content
    )
}
