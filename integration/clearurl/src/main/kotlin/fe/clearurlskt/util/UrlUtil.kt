package fe.clearurlskt.util

import fe.std.extension.takeIfNotEmpty
import fe.std.uri.StdUrl

public fun StdUrl.toFragmentMap(): MutableMap<String, String?> {
    return fragment
        ?.split("&")
        ?.map { it.split("=") }
        ?.associateTo(LinkedHashMap()) { it[0] to it.getOrNull(1) }
        ?: mutableMapOf()
}

public fun StdUrl.toFragmentList(): List<Pair<String, String?>> {
    return fragment
        ?.split("&")
        ?.mapNotNull { it.split("=").takeIfNotEmpty() }
        ?.map {
            it[0] to it.getOrNull(1)
        }
        ?: emptyList()
}

public fun Map<String, String?>.keyValueMapToString(): String {
    return map { (key, value) ->
        if (value != null) "$key=$value"
        else key
    }.joinToString("&")
}

public fun List<Pair<String, String?>>.keyValueListToString(): String {
    return joinToString("&") { (key, value) ->
        if (value != null) "$key=$value"
        else key
    }
}
