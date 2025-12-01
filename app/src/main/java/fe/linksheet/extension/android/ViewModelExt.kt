package fe.linksheet.extension.android

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


inline fun <T> ViewModel.launchIO(
    crossinline block: suspend CoroutineScope.() -> T
) = viewModelScope.launch(Dispatchers.IO) { block(this) }
