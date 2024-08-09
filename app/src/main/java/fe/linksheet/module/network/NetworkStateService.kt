package fe.linksheet.module.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleOwner
import fe.android.lifecycle.LifecycleService
import fe.linksheet.extension.koin.service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import org.koin.dsl.module


val networkStateServiceModule = module {
    service<NetworkStateService> {
        val connectivityManager = applicationContext.getSystemService<ConnectivityManager>()!!

        NetworkStateService(connectivityManager)
    }
}

class NetworkStateService(
    private val connectivityManager: ConnectivityManager,
) : LifecycleService, ConnectivityManager.NetworkCallback() {

    private val _currentNetwork = MutableStateFlow(ConnectedNetwork.Unknown)
    val currentNetwork = _currentNetwork.asStateFlow()

    val isNetworkConnected: Boolean
        get() = _currentNetwork.value.isConnected

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        connectivityManager.registerDefaultNetworkCallback(this)
    }

    suspend fun awaitNetworkConnection(): ConnectedNetwork {
        return if (isNetworkConnected) _currentNetwork.value
        else _currentNetwork.first { it.isConnected }
    }

    override fun onAvailable(network: Network) {
        _currentNetwork.update { it.copy(isAvailable = true) }
    }

    override fun onLost(network: Network) {
        _currentNetwork.update { it.copy(isAvailable = false, networkCapabilities = null) }
    }

    override fun onUnavailable() {
        _currentNetwork.update { it.copy(isAvailable = false, networkCapabilities = null) }
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        _currentNetwork.update { it.copy(networkCapabilities = networkCapabilities) }
    }

    override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
        _currentNetwork.update { it.copy(isBlocked = blocked) }
    }
}


