package fe.linksheet.experiment.ui.overhaul.composable.component.page

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import fe.linksheet.experiment.ui.overhaul.composable.util.IconType
import fe.linksheet.experiment.ui.overhaul.composable.util.OptionalTextContent
import fe.linksheet.experiment.ui.overhaul.composable.util.TextContent

interface GroupValueProvider<K : Any> {
    val key: K
}

@Stable
open class ListItemData<T>(
    val icon: IconType? = null,
    val headlineContent: TextContent,
    val subtitleContent: OptionalTextContent = null,
    val additional: T? = null
) : GroupValueProvider<Any> {
    override val key = headlineContent.key
}

@DslMarker
annotation class RememberGroupDslMarker

@RememberGroupDslMarker
open class RememberGroupScope<K : Any, T : GroupValueProvider<K>>(
    private val _providers: MutableList<T> = mutableListOf(),
) {
    val providers: List<T>
        get() = _providers

    fun add(provider: T) {
        _providers.add(provider)
    }
}
