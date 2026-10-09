package app.linksheet.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

interface RefreshableDataFlow<T, R> {
    val flow: Flow<T>
    suspend fun refresh(refresh: R)
}

fun <T, R> RefreshableDataFlow(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    initialState: T,
    loadData: suspend FlowCollector<T>.(currentState: T) -> Unit,
    refreshData: (suspend FlowCollector<T>.(currentState: T, trigger: R) -> Unit)? = null,
): RefreshableDataFlow<T, R> {
    val handle = RefreshableDataFlowInternal<T, R>(dispatcher)
    val flow = handle.createFlow(initialState, loadData, refreshData)

    return object : RefreshableDataFlow<T, R> {
        override val flow: Flow<T> = flow
        override suspend fun refresh(refresh: R) {
            handle.refresh(refresh)
        }
    }
}

internal class RefreshableDataFlowInternal<T, R>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
)  {
    private val ticker = MutableSharedFlow<R>()

    fun createFlow(
        initialState: T,
        loadData: suspend FlowCollector<T>.(currentState: T) -> Unit,
        refreshData: (suspend FlowCollector<T>.(currentState: T, triggerParams: R) -> Unit)? = null,
    ): Flow<T> {
        var latestValue = initialState
        return flow {
            emit(latestValue)
            loadData(latestValue)
            if (refreshData != null) {
                ticker.collect { triggerParams ->
                    refreshData(this, latestValue, triggerParams)
                }
            }
        }
            .flowOn(dispatcher)
            .distinctUntilChanged()
            .onEach {
                latestValue = it
            }
//            .stateIn(
//                scope = scope,
//                started = SharingStarted.WhileSubscribed(timeout),
//                initialValue = initialState
//            )
    }

    suspend fun refresh(refresh: R) = withContext(dispatcher) {
        ticker.emit(refresh)
    }
}
