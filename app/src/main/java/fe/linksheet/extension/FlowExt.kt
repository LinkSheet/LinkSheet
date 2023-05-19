package fe.linksheet.extension

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> Flow<T>.ioState(initialValue: T? = null) = collectAsStateWithLifecycle(
    initialValue = initialValue,
    context = Dispatchers.IO
)

@Composable
fun <T> StateFlow<T>.ioState() = collectAsStateWithLifecycle(context = Dispatchers.IO)
