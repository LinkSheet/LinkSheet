package fe.linksheet.module.preference

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlin.reflect.KProperty

class RepositoryState<T, NT, P : BasePreference<T, NT>>(
    private val preference: P,
    private val writer: (P, NT) -> Unit,
    initialValue: NT,
) {
    private val mutableState = mutableStateOf(initialValue)
    val value by mutableState

    fun matches(toMatch: NT) = value == toMatch

    fun updateState(newState: NT) {
        if (mutableState.value != newState) {
            mutableState.value = newState
            writer(preference, newState)
        }
    }

    operator fun <T> getValue(thisObj: Any?, property: KProperty<*>): NT = value
}