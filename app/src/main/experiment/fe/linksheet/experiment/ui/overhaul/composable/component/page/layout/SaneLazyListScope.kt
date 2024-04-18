package fe.linksheet.experiment.ui.overhaul.composable.component.page.layout

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.page.GroupValueProvider

@DslMarker
annotation class SaneLazyListScopeDslMarker

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
