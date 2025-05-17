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

    fun isConnected(): Boolean {
        if (networkCapabilities == null) return false
        return isAvailable && !isBlocked
                && CAPABILITIES.all(networkCapabilities::hasCapability)
                && TRANSPORTS.any(networkCapabilities::hasTransport)
    }
}

