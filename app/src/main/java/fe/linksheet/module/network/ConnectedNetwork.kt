package fe.linksheet.module.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

data class ConnectedNetwork(
    val networkCapabilities: NetworkCapabilities?,
    val isAvailable: Boolean,
    val isBlocked: Boolean,
) {
    companion object {
        /**
         * On Android 9, [ConnectivityManager.NetworkCallback.onBlockedStatusChanged] is not called when
         * we call the [ConnectivityManager.registerDefaultNetworkCallback] function.
         * Hence we assume that the network is unblocked by default.
         */
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

    val isConnected by lazy {
        if (networkCapabilities == null) false
        else isAvailable && !isBlocked
                && CAPABILITIES.all { networkCapabilities.hasCapability(it) }
                && TRANSPORTS.any { networkCapabilities.hasTransport(it) }
    }
}

