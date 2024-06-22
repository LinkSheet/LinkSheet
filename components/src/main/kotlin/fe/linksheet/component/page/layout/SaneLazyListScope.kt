package fe.linksheet.component.page.layout

import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource
import fe.linksheet.component.ContentTypeDefaults

@DslMarker
annotation class SaneLazyListScopeDslMarker

@Stable
data class SaneLazyListScopeImpl(val lazyListScope: LazyListScope) : SaneLazyListScope, LazyListScope by lazyListScope {
    private fun dividerKey(stringRes: Int, key: Any) = if (key == stringRes) "$key-1" else key

    override fun divider(stringRes: Int, key: Any) {
        item(key = dividerKey(stringRes, key), contentType = ContentTypeDefaults.Divider) {
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
