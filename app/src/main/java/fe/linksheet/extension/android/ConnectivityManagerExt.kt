package fe.linksheet.extension.android

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun ConnectivityManager.canAccessInternet(): Boolean {
    val capabilities = getNetworkCapabilities(activeNetwork) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

