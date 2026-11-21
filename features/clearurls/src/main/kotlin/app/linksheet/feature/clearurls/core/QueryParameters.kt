package app.linksheet.feature.clearurls.core

/**
 * Range of the query of [url], starting at its `?` and ending before the fragment, or `null` if
 * [url] has no query. A `?` which only appears inside the fragment does not start a query.
 */
public fun queryRange(url: String): IntRange? {
    val start = url.substringBefore('#').indexOf('?')
    if (start < 0) return null

    val fragmentStart = url.indexOf('#')
    return start until if (fragmentStart >= 0) fragmentStart else url.length
}

/**
 * Removes the entire query of [url], leaving path and fragment untouched.
 */
public fun removeAllQueryParameters(url: String): String {
    val range = queryRange(url) ?: return url
    return url.removeRange(range)
}
