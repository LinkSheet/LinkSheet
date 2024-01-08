package fe.linksheet.composable.util

enum class ListState {
    Loading, NoItems, NoResult, Items;
}

fun <T> listState(list: List<T>?, filter: String) = if (list == null) {
    ListState.Loading
} else {
    if (list.isNotEmpty()) ListState.Items
    else if (filter.isEmpty()) ListState.NoItems else ListState.NoResult
}

fun <T> listState(list: List<T>?) = if (list == null) {
    ListState.Loading
} else {
    if (list.isNotEmpty()) ListState.Items
    else ListState.NoItems
}

fun <K, V> mapState(list: Map<K, V>?, filter: String) = if (list == null) {
    ListState.Loading
} else {
    if (list.isNotEmpty()) ListState.Items
    else if (filter.isEmpty()) ListState.NoItems else ListState.NoResult
}

fun <K, V> mapState(list: Map<K, V>?) = if (list == null) {
    ListState.Loading
} else {
    if (list.isNotEmpty()) ListState.Items
    else ListState.NoItems
}
