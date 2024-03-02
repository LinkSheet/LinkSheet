package fe.linksheet.module.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

data class CurrentNetwork(
    val isListening: Boolean,
    val networkCapabilities: NetworkCapabilities?,
    val isAvailable: Boolean,
    val isBlocked: Boolean
) {
    companion object {
        /**
         * On Android 9, [ConnectivityManager.NetworkCallback.onBlockedStatusChanged] is not called when
         * we call the [ConnectivityManager.registerDefaultNetworkCallback] function.
         * Hence we assume that the network is unblocked by default.
         */
        val Default = CurrentNetwork(
            isListening = false,
            networkCapabilities = null,
            isAvailable = false,
            isBlocked = false
        )

        private val CAPABILITIES = arrayOf(
            NetworkCapabilities.NET_CAPABILITY_INTERNET, NetworkCapabilities.NET_CAPABILITY_VALIDATED
        )

        private val TRANSPORTS = arrayOf(
            NetworkCapabilities.TRANSPORT_WIFI,
            NetworkCapabilities.TRANSPORT_CELLULAR,
            NetworkCapabilities.TRANSPORT_ETHERNET
        )
    }

    fun isConnected(): Boolean {
        if (networkCapabilities == null) return false
        return isListening
                && isAvailable
                && !isBlocked
                && CAPABILITIES.all { networkCapabilities.hasCapability(it) }
                && TRANSPORTS.any { networkCapabilities.hasTransport(it) }
    }
}

