package fe.linksheet.extension

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

inline fun <T> ViewModel.ioAsync(
    crossinline block: suspend CoroutineScope.() -> T
) = viewModelScope.async(Dispatchers.IO) { block(this) }

inline fun <T> ViewModel.ioAsync(
    crossinline loading: (Boolean) -> Unit,
    crossinline block: suspend CoroutineScope.() -> T
) = ioAsync {
    loading(true)
    val result = block()
    loading(false)

    result
}

inline fun <T> ViewModel.ioAsync(
    loading: MutableState<Boolean>,
    crossinline block: suspend CoroutineScope.() -> T
) = ioAsync({ loading.value = it }, block)

inline fun <T> ViewModel.ioAsync(
    list: MutableList<T>,
    crossinline block: suspend CoroutineScope.() -> Iterable<T>
) = ioAsync { list.setup(block()) }

inline fun <T> ViewModel.ioAsync(
    loading: MutableState<Boolean>,
    list: MutableList<T>,
    crossinline block: suspend CoroutineScope.() -> Iterable<T>
) = ioAsync {
    loading.value = true
    list.setup(block())
    loading.value = false
}

inline fun <T> ViewModel.ioLaunch(
    crossinline block: suspend CoroutineScope.() -> T
) = viewModelScope.launch(Dispatchers.IO) { block(this) }