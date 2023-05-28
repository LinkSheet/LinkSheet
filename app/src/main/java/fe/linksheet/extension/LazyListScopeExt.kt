package fe.linksheet.extension

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import fe.linksheet.composable.util.ListState

fun <K, V> LazyListScope.items(
    items: Map<K, V>,
    key: ((K) -> Any)? = null,
    contentType: (K) -> Any? = { null },
    itemContent: @Composable LazyItemScope.(K, V) -> Unit
) = items.forEach { (k, v) ->
    item(key?.invoke(k), contentType.invoke(k)) {
        itemContent(k, v)
    }
}

inline fun <T> LazyListScope.listHelper(
    @StringRes noItems: Int,
    @StringRes notFound: Int? = null,
    listState: ListState,
    list: List<T>?,
    noinline listKey: ((T) -> Any)? = null,
    crossinline listItem: @Composable LazyItemScope.(item: T) -> Unit,
) {
    if (listState == ListState.Items) {
        items(items = list!!, key = listKey, itemContent = listItem)
    } else {
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
}