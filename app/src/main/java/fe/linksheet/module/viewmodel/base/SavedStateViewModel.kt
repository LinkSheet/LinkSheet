package fe.linksheet.module.viewmodel.base

import androidx.lifecycle.SavedStateHandle
import fe.android.preference.helper.PreferenceRepository
import fe.linksheet.util.RouteData
import kotlin.reflect.KProperty1

abstract class SavedStateViewModel<T : RouteData>(
    private val savedStateHandle: SavedStateHandle,
    preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    fun <T, V> getSavedState(property: KProperty1<T, V>) = savedStateHandle.get<V>(property.name)
}
