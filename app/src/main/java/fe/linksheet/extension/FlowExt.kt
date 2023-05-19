package fe.linksheet.extension

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull

@Composable
fun <T> Flow<T>.ioState(initialValue: T? = null) = collectAsStateWithLifecycle(
    initialValue = initialValue,
    context = Dispatchers.IO
)

@Composable
fun <T> StateFlow<T>.ioState() = collectAsStateWithLifecycle(context = Dispatchers.IO)
