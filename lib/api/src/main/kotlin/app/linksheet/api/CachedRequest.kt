package app.linksheet.api

import java.io.IOException
import java.net.HttpURLConnection

interface CachedRequest {
    @Throws(IOException::class)
    fun head(url: String, timeout: Int, followRedirects: Boolean): HttpURLConnection

    @Throws(IOException::class)
    fun get(url: String, timeout: Int, followRedirects: Boolean): HttpURLConnection
}
