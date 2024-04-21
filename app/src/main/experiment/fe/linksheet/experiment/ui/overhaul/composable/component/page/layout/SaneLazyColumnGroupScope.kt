package fe.linksheet.experiment.ui.overhaul.composable.component.page.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Shape
import fe.linksheet.experiment.ui.overhaul.composable.component.page.GroupValueProvider

@DslMarker
annotation class LazyGroupScopeMarker

@LazyGroupScopeMarker
interface SaneLazyColumnGroupScope {
    fun item(key: Any, content: @Composable LazyItemScope.(PaddingValues, Shape) -> Unit)

    fun <K : Any, T> items(
        values: List<T>,
        key: (T) -> K,
        content: @Composable LazyItemScope.(T, PaddingValues, Shape) -> Unit,
    )

    fun <K : Any, T : GroupValueProvider<K>> items(
        values: Array<T>,
        content: @Composable LazyItemScope.(T, PaddingValues, Shape) -> Unit,
    )

    fun <K : Any, T : GroupValueProvider<K>> items(
        values: List<T>,
        content: @Composable LazyItemScope.(T, PaddingValues, Shape) -> Unit,
    )

    fun <K : Any, T : GroupValueProvider<K>, V> items(
        values: Map<T, V>,
        content: @Composable LazyItemScope.(T, V, PaddingValues, Shape) -> Unit,
    )
}


@Stable
data class SaneLazyColumnGroupScopeImpl(
    val size: Int,
    val listScope: SaneLazyListScope,
) : SaneLazyColumnGroupScope, SaneLazyListScope by listScope {

    private var counter = 0

    private fun currentItem(): GroupItem {
        if (size == 1) return GroupItem.Single

        return when (counter) {
            0 -> GroupItem.Top
            size - 1 -> GroupItem.Bottom
            else -> GroupItem.Middle
        }
    }

    override fun item(key: Any, content: @Composable LazyItemScope.(PaddingValues, Shape) -> Unit) {
        require(counter < size) { "Group has ${counter + 1} items, but only supports $size" }

        val groupItem = currentItem()
        item(key = key, contentType = groupItem.contentType) {
            content(groupItem.padding, groupItem.shape)
        }

        counter++
    }

    override fun <K : Any, T : GroupValueProvider<K>> items(
        values: Array<T>,
        content: @Composable (LazyItemScope.(T, PaddingValues, Shape) -> Unit),
    ) {
        itemsInternal(valueSize = values.size, values = values.iterator(), key = { it.key }, content = content)
    }

    override fun <K : Any, T : GroupValueProvider<K>> items(
        values: List<T>,
        content: @Composable (LazyItemScope.(T, PaddingValues, Shape) -> Unit),
    ) {
        itemsInternal(valueSize = values.size, values = values.iterator(), key = { it.key }, content = content)
    }

    override fun <K : Any, T> items(
        values: List<T>,
        key: (T) -> K,
        content: @Composable (LazyItemScope.(T, PaddingValues, Shape) -> Unit),
    ) {
        itemsInternal(valueSize = values.size, values = values.iterator(), key = key, content = content)
    }

    private inline fun <K : Any, T> itemsInternal(
        valueSize: Int,
        values: Iterator<T>,
        key: (T) -> K,
        crossinline content: @Composable (LazyItemScope.(T, PaddingValues, Shape) -> Unit),
    ) {
        require(counter < size) { "Group has ${counter + 1} items, but only supports $size" }
        require(counter + valueSize <= size) { "Group has ${counter + 1}/$size items, can't fit an additional $valueSize" }

        for (value in values) {
            val groupItem = currentItem()
            item(key = key(value), contentType = groupItem) {
                content(value, groupItem.padding, groupItem.shape)
            }

            counter++
        }
    }


    override fun <K : Any, T : GroupValueProvider<K>, V> items(
        values: Map<T, V>,
        content: @Composable (LazyItemScope.(T, V, PaddingValues, Shape) -> Unit),
    ) {
        require(counter < size) { "Group has ${counter + 1} items, but only supports $size" }
        require(counter + values.size <= size) { "Group has ${counter + 1}/$size items, can't fit an additional ${values.size}" }

        for ((valueProvider, value) in values) {
            val groupItem = currentItem()
            item(key = valueProvider.key, contentType = groupItem) {
                content(valueProvider, value, groupItem.padding, groupItem.shape)
            }

            counter++
        }
    }
}

fun <K : Any, T : GroupValueProvider<K>> SaneLazyListScope.group(
    items: Array<T>,
    content: @Composable (LazyItemScope.(T, PaddingValues, Shape) -> Unit),
) {
    group(size = items.size) { items(values = items, content = content) }
}

fun <K : Any, T : GroupValueProvider<K>> SaneLazyListScope.group(
    items: List<T>,
    content: @Composable (LazyItemScope.(T, PaddingValues, Shape) -> Unit),
) {
    group(size = items.size) { items(values = items, content = content) }
}

fun <K : Any, T> SaneLazyListScope.group(
    items: List<T>,
    key: (T) -> K,
    content: @Composable (LazyItemScope.(T, PaddingValues, Shape) -> Unit),
) {
    group(size = items.size) { items(values = items, key = key, content = content) }
}
