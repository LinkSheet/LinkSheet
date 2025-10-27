package fe.linksheet.web

import android.net.Uri

enum class Darknet(val displayName: String, val tld: String) {
    I2P("I2P", ".i2p"),
    Tor("Tor", ".onion");

    companion object {
        fun getOrNull(uri: Uri): Darknet? {
            return entries.find { uri.host?.endsWith(it.tld) == true }
        }
    }
}
