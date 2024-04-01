package fe.linksheet.module.analytics

sealed class AnalyticsEvent(val name: String, val data: Map<String, Any>) {
    val unixMillis = System.currentTimeMillis()

    constructor(name: String, vararg pairs: Pair<String, Any>) : this(name, mapOf(*pairs))
    constructor(name: String, map: Map<String, Any>, vararg pairs: Pair<String, Any>) : this(name, map + mapOf(*pairs))

    data class Navigate(val destination: String) : AnalyticsEvent("navigate", mapOf("destination" to destination))
}

sealed class AppStart(
    type: String, vararg data: Pair<String, Any>,
) : AnalyticsEvent("app_start", mapOf("type" to type), *data) {
    data object FirstRun : AppStart("first")

    data object Default : AppStart("normal")

    data class Updated(val lastVersion: Int) : AppStart("updated", "last_version" to lastVersion)
}
