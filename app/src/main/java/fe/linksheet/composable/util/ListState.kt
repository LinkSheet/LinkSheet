package fe.linksheet.composable.util

enum class ListState {
    Loading, NoItems, NoResult, Items;
}

fun <T> listState(list: List<T>?, filter: String): ListState {
    return if (list == null) ListState.Loading else {
        if (list.isNotEmpty()) ListState.Items
        else if (filter.isEmpty()) ListState.NoItems else ListState.NoResult
    }
}

fun <T> listState(list: List<T>?): ListState {
    return if (list == null) ListState.Loading else {
        if (list.isNotEmpty()) ListState.Items
        else ListState.NoItems
    }
}

fun <K, V> mapState(list: Map<K, V>?, filter: String): ListState {
    return if (list == null) ListState.Loading else {
        if (list.isNotEmpty()) ListState.Items
        else if (filter.isEmpty()) ListState.NoItems else ListState.NoResult
    }
}

fun <K, V> mapState(map: Map<K, V>?): ListState {
    return if (map == null) ListState.Loading else {
        if (map.isNotEmpty()) ListState.Items
        else ListState.NoItems
    }
}
