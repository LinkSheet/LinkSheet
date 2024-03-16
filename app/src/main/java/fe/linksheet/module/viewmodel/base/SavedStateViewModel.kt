package fe.linksheet.module.viewmodel.base

import androidx.lifecycle.SavedStateHandle
import fe.android.compose.route.util.RouteData
import fe.linksheet.module.preference.app.AppPreferenceRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KProperty1

abstract class SavedStateViewModel<T : RouteData>(
    private val savedStateHandle: SavedStateHandle,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    fun <T, V> getSavedState(property: KProperty1<T, V>) = savedStateHandle.get<V>(property.name).let {
        if(it == "null") null else it
    }

    fun <T, V> getSavedStateFlowNullable(
        property: KProperty1<T, V>
    ) = MutableStateFlow(getSavedState(property))

    fun <T, V> getSavedStateFlow(
        property: KProperty1<T, V>
    ) = MutableStateFlow(getSavedState(property)!!)
}
