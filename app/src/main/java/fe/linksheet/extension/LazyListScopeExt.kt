package fe.linksheet.extension

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable

fun <K, V> LazyListScope.items(
    map: Map<K, V>,
    key: ((K) -> Any)? = null,
    contentType: ((K) -> Any)? = null,
    content: @Composable LazyItemScope.(K, V) -> Unit
) = map.forEach { (k, v) ->
    item(key?.invoke(k), contentType?.invoke(k)) {
        content(k, v)
    }
}
