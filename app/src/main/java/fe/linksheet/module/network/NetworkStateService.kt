package fe.linksheet.module.network

import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import fe.linksheet.extension.koin.service
import fe.linksheet.module.lifecycle.Service
import kotlinx.coroutines.flow.*
import org.koin.dsl.module


val networkStateServiceModule = module {
    service<NetworkStateService> { NetworkStateService(applicationContext.getSystemService()!!) }
}

class NetworkStateService(private val connectivityManager: ConnectivityManager) : Service {

    private val currentNetwork = MutableStateFlow(CurrentNetwork.Default)
    private val networkCallback = NetworkCallback(currentNetwork)

    private lateinit var isNetworkConnectedFlow: StateFlow<Boolean>

    val isNetworkConnected: Boolean
        get() = isNetworkConnectedFlow.value


    override fun start(lifecycle: Lifecycle) {
        if (currentNetwork.value.isListening) return

        isNetworkConnectedFlow = currentNetwork.map { it.isConnected() }.stateIn(
            scope = lifecycle.coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = currentNetwork.value.isConnected()
        )

        currentNetwork.update { CurrentNetwork.Default.copy(isListening = true) }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun stop(lifecycle: Lifecycle) {
        if (!currentNetwork.value.isListening) return

        currentNetwork.update { CurrentNetwork.Default.copy(isListening = false) }
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }


    suspend fun awaitNetworkConnection(): Boolean {
        return if (isNetworkConnected) isNetworkConnected
        else isNetworkConnectedFlow.first { it }
    }
}


