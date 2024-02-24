package fe.linksheet.util

const val httpsScheme = "https://"

// TODO: Does not look very robust
fun cleanHttpsScheme(host: String): String {
    val hostWithoutScheme = if (host.indexOf(httpsScheme) != -1) {
        host.substring(httpsScheme.length)
    } else host

    return if (hostWithoutScheme.endsWith("/")) hostWithoutScheme.substring(
        0,
        hostWithoutScheme.length - 2
    ) else hostWithoutScheme
}
