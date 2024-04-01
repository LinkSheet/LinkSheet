package fe.linksheet.experiment.ui.overhaul.composable.component.page

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults

@Stable
data class GroupItem(
    val contentType: ContentTypeDefaults,
    val padding: PaddingValues = PaddingValues(),
    val shape: Shape,
) {
    companion object {
        val Top = GroupItem(
            ContentTypeDefaults.TopGroupItem,
            SaneLazyColumnPageDefaults.GroupSpacingTop,
            ShapeListItemDefaults.TopShape
        )

        val Middle = GroupItem(
            ContentTypeDefaults.MiddleGroupItem,
            SaneLazyColumnPageDefaults.GroupSpacingMiddle,
            ShapeListItemDefaults.MiddleShape
        )

        val Bottom = GroupItem(
            ContentTypeDefaults.BottomGroupItem,
            SaneLazyColumnPageDefaults.GroupSpacingBottom,
            ShapeListItemDefaults.BottomShape
        )

        val Single = GroupItem(
            ContentTypeDefaults.SingleGroupItem,
            shape = ShapeListItemDefaults.SingleShape
        )
    }
}

object SaneLazyColumnPageDefaults {
    val VerticalSpacing = 12.dp
    val ContentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = VerticalSpacing, bottom = 6.dp)

    val TextDividerPadding = PaddingValues(
        start = 16.dp,
        top = VerticalSpacing,
        bottom = VerticalSpacing
    )

    val GroupSpacingTop = PaddingValues(bottom = 1.dp)
    val GroupSpacingMiddle = PaddingValues(vertical = 1.dp)
    val GroupSpacingBottom = PaddingValues(top = 1.dp)


}

@Composable
fun SaneLazyColumnPageLayout(
    padding: PaddingValues,
    contentPadding: PaddingValues = SaneLazyColumnPageDefaults.ContentPadding,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: SaneLazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        content = { content(SaneLazyListScopeImpl(this)) }
    )
}

@DslMarker
annotation class LazyGroupScopeMarker

interface GroupValueProvider<K : Any> {
    val key: K
}

@LazyGroupScopeMarker
interface SaneLazyColumnGroupScope {
    fun item(key: Any, content: @Composable LazyItemScope.(PaddingValues, Shape) -> Unit)

    fun <K : Any, T : GroupValueProvider<K>> items(
        values: Array<T>,
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
        require(counter < size) { "Group has ${counter + 1} items, but only supports $size" }
        require(counter + values.size < size) { "Group has ${counter + 1}/$size items, can't fit an additional ${values.size}" }

        for (value in values) {
            val groupItem = currentItem()
            item(key = value.key, contentType = groupItem) {
                content(value, groupItem.padding, groupItem.shape)
            }

            counter++
        }
    }

    override fun <K : Any, T : GroupValueProvider<K>, V> items(
        map: Map<T, V>,
        content: @Composable (LazyItemScope.(T, V, PaddingValues, Shape) -> Unit),
    ) {
        require(counter < size) { "Group has ${counter + 1} items, but only supports $size" }
        require(counter + map.size <= size) { "Group has ${counter + 1}/$size items, can't fit an additional ${map.size}" }

        for ((valueProvider, value) in map) {
            val groupItem = currentItem()
            item(key = valueProvider.key, contentType = groupItem) {
                content(valueProvider, value, groupItem.padding, groupItem.shape)
            }

            counter++
        }
    }
}

@DslMarker
annotation class SaneLazyListScopeDslMarker

@Composable
fun TextDivider(text: String, padding: PaddingValues = SaneLazyColumnPageDefaults.TextDividerPadding) {
    Text(
        modifier = Modifier.padding(padding),
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleSmall
    )
}

@Stable
data class SaneLazyListScopeImpl(val lazyListScope: LazyListScope) : SaneLazyListScope, LazyListScope by lazyListScope {
    override fun divider(stringRes: Int, key: Any) {
        item(key = key, contentType = ContentTypeDefaults.Divider) {
            TextDivider(text = stringResource(id = stringRes))
        }
    }

    override fun divider(key: Any, text: String) {
        item(key = key, contentType = ContentTypeDefaults.Divider) {
            TextDivider(text = text)
        }
    }

    override fun group(size: Int, content: SaneLazyColumnGroupScope.() -> Unit) {
        require(size > 0) { "Group size must be greater than 0" }
        SaneLazyColumnGroupScopeImpl(size, this).apply(content)
    }
}

@SaneLazyListScopeDslMarker
interface SaneLazyListScope : LazyListScope {
    fun divider(@StringRes stringRes: Int, key: Any = stringRes)

    fun divider(key: Any, text: String)

    fun group(size: Int, content: SaneLazyColumnGroupScope.() -> Unit)
}
