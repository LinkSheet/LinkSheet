package fe.linksheet.extension.compose

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.composable.util.PreferenceSubtitle
import fe.linksheet.composable.util.ListState
import fe.linksheet.composable.util.Searchbar
import fe.linksheet.experiment.ui.overhaul.composable.component.page.GroupValueProvider
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.SaneLazyListScope
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.group
import kotlinx.coroutines.flow.MutableStateFlow

fun LazyListScope.spacer(height: Int = 10, itemKey: Any? = null) {
    item(key = itemKey) {
        Spacer(modifier = Modifier.height(height.dp))
    }
}

fun LazyListScope.header(@StringRes header: Int, itemKey: Any? = null, height: Int = 10) {
    item(key = itemKey) {
        Text(modifier = Modifier.padding(horizontal = 10.dp), text = stringResource(id = header))
        Spacer(modifier = Modifier.height(height.dp))
    }
}

inline fun <K, V> LazyListScope.items(
    items: Map<K, V>,
    key: (K) -> Any,
    contentType: (K) -> Any? = { null },
    crossinline itemContent: @Composable LazyItemScope.(K, V) -> Unit,
) = items.forEach { (k, v) ->
    item(key.invoke(k), contentType.invoke(k)) {
        itemContent(k, v)
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.searchHeader(
    @StringRes subtitleId: Int,
    filter: String,
    searchFilter: MutableStateFlow<String>,
) {
    stickyHeader(key = "header") {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            PreferenceSubtitle(
                text = stringResource(subtitleId),
                paddingHorizontal = 0.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Searchbar(filter = filter, onFilterChanged = {
                searchFilter.value = it
            })

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

inline fun <T> LazyListScope.listHelper(
    @StringRes noItems: Int,
    @StringRes notFound: Int? = null,
    listState: ListState,
    list: List<T>?,
    noinline listKey: (T) -> Any,
    crossinline listItem: @Composable LazyItemScope.(T) -> Unit,
) {
    if (listState == ListState.Items && list != null) {
        items(items = list, key = listKey, itemContent = listItem)
    } else {
        loader(noItems, notFound, listState)
    }
}

fun <T> SaneLazyListScope.listHelper(
    @StringRes noItems: Int,
    @StringRes notFound: Int? = null,
    listState: ListState,
    list: List<T>?,
    listKey: (T) -> Any,
    content: @Composable LazyItemScope.(T, PaddingValues, Shape) -> Unit,
) {
    if (listState == ListState.Items && !list.isNullOrEmpty()) {
        group(items = list, key = listKey, content = content)
    } else {
        loader(noItems, notFound, listState)
    }
}

fun <K : Any, T : GroupValueProvider<K>, V> SaneLazyListScope.mapHelper(
    @StringRes noItems: Int,
    @StringRes notFound: Int? = null,
    mapState: ListState,
    values: Map<T, V>?,
    content: @Composable LazyItemScope.(T, V, PaddingValues, Shape) -> Unit,
) {
    if (mapState == ListState.Items) {
        group(values = values!!, content = content)
    } else {
        loader(noItems, notFound, mapState)
    }
}

inline fun <K, V> LazyListScope.mapHelper(
    @StringRes noItems: Int,
    @StringRes notFound: Int? = null,
    mapState: ListState,
    map: Map<K, V>?,
    listKey: (K) -> Any,
    crossinline listItem: @Composable LazyItemScope.(K, V) -> Unit,
) {
    if (mapState == ListState.Items) {
        items(items = map!!, key = listKey, itemContent = listItem)
    } else {
        loader(noItems, notFound, mapState)
    }
}

fun LazyListScope.loader(
    @StringRes noItems: Int,
    @StringRes notFound: Int? = null,
    listState: ListState,
) {
    item(key = "loader") {
        Column(
            modifier = Modifier
                .fillParentMaxWidth()
                .fillParentMaxHeight(0.4f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (notFound != null && listState == ListState.NoResult) {
                Text(text = stringResource(id = notFound))
            }

            when (listState) {
                ListState.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                ListState.NoItems -> Text(text = stringResource(id = noItems))
                else -> {}
            }
        }
    }
}
