package fe.linksheet.module.network

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val networkStateModule = module {
    singleOf(::NetworkState)
}

class NetworkState(val context: Context, coroutineScope: LifecycleCoroutineScope) {
    private val connectivityManager: ConnectivityManager = context.getSystemService()!!
    private val currentNetwork = MutableStateFlow(CurrentNetwork.Default)

    private val networkCallback = NetworkCallback(currentNetwork)

    val isNetworkConnectedFlow: StateFlow<Boolean> = currentNetwork
        .map { it.isConnected() }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = currentNetwork.value.isConnected()
        )

    val isNetworkConnected: Boolean
        get() = isNetworkConnectedFlow.value

    fun registerListener() {
        if (currentNetwork.value.isListening) return

        currentNetwork.update { CurrentNetwork.Default.copy(isListening = true) }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    fun unregisterListener() {
        if (!currentNetwork.value.isListening) return

        currentNetwork.update { CurrentNetwork.Default.copy(isListening = false) }
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    suspend fun awaitNetworkConnection(): Boolean {
        return if (isNetworkConnected) isNetworkConnected
        else isNetworkConnectedFlow.first { it }
    }
}


