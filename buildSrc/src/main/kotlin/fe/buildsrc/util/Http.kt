package fe.buildsrc.util

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest


fun get(url: String): HttpRequest {
    return HttpRequest.newBuilder()
        .uri(URI(url))
        .GET()
        .build()
}

val httpClient: HttpClient by lazy {
    HttpClient.newHttpClient()
}
