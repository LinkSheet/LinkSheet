package fe.linksheet.experiment.ui.overhaul.composable.component.page

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable

interface GroupValueProvider<K : Any> {
    val key: K
}

@Stable
open class TwoLineGroupValueProvider(
    @StringRes val headlineId: Int,
    @StringRes val subtitleId: Int,
) : GroupValueProvider<Int> {
    override val key = headlineId
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
