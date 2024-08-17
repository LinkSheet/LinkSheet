package fe.linksheet.module.network

import android.net.NetworkCapabilities

data class ConnectedNetwork(
    val networkCapabilities: NetworkCapabilities?,
    val isAvailable: Boolean,
    val isBlocked: Boolean,
) {
    companion object {
        val Unknown = ConnectedNetwork(
            networkCapabilities = null,
            isAvailable = false,
            isBlocked = false
        )

        private val CAPABILITIES = arrayOf(
            NetworkCapabilities.NET_CAPABILITY_INTERNET,
            NetworkCapabilities.NET_CAPABILITY_VALIDATED
        )

        private val TRANSPORTS = arrayOf(
            NetworkCapabilities.TRANSPORT_WIFI,
            NetworkCapabilities.TRANSPORT_CELLULAR,
            NetworkCapabilities.TRANSPORT_ETHERNET
        )
    }

    val isConnected = networkCapabilities?.let { cap ->
        isAvailable && !isBlocked && CAPABILITIES.all(cap::hasCapability) && TRANSPORTS.any(cap::hasTransport)
    } ?: false
}

