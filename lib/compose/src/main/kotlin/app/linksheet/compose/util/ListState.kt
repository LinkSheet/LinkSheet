package app.linksheet.compose.util

enum class ListState {
    Loading, NoItems, NoResult, Items;
}

fun <T> listState(list: List<T>?, filter: String): ListState {
    return when {
        list == null -> ListState.Loading
        list.isNotEmpty() -> ListState.Items
        filter.isEmpty() -> ListState.NoItems
        else -> ListState.NoResult
    }
}

fun <T> listState(list: List<T>?): ListState {
    return when {
        list == null -> ListState.Loading
        list.isNotEmpty() -> ListState.Items
        else -> ListState.NoItems
    }
}

fun <K, V> mapState(list: Map<K, V>?, filter: String): ListState {
    return when {
        list == null -> ListState.Loading
        list.isNotEmpty() -> ListState.Items
        filter.isEmpty() -> ListState.NoItems
        else -> ListState.NoResult
    }
}

fun <K, V> mapState(map: Map<K, V>?): ListState {
    return when {
        map == null -> ListState.Loading
        map.isNotEmpty() -> ListState.Items
        else -> ListState.NoItems
    }
}
