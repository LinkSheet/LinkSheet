package fe.linksheet.module.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class NetworkCallback(
    private val currentNetwork: MutableStateFlow<CurrentNetwork>
) : ConnectivityManager.NetworkCallback() {

    override fun onAvailable(network: Network) {
        currentNetwork.update { it.copy(isAvailable = true) }
    }

    override fun onLost(network: Network) {
        currentNetwork.update { it.copy(isAvailable = false, networkCapabilities = null) }
    }

    override fun onUnavailable() {
        currentNetwork.update { it.copy(isAvailable = false, networkCapabilities = null) }
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        currentNetwork.update { it.copy(networkCapabilities = networkCapabilities) }
    }

    override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
        currentNetwork.update { it.copy(isBlocked = blocked) }
    }
}
