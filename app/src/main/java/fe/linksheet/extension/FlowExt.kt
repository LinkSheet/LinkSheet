package fe.linksheet.extension

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
private fun <T> Flow<T>.collectOnIO(initialState: T): State<T> {
    return collectAsStateWithLifecycle(initialValue = initialState, context = Dispatchers.IO)
}

@Composable
fun <T> Flow<T>.collectOnIO(): State<T?> {
    return collectOnIO(null)
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun <T> StateFlow<T>.collectOnIO(): State<T> {
    return collectOnIO(value)
}
