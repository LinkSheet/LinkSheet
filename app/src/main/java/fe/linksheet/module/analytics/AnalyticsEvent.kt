package fe.linksheet.module.analytics

import com.google.gson.JsonObject
import fe.gson.dsl.jsonObject
import fe.gson.extension.json.element.stringOrNull

sealed class AnalyticsEvent(val name: String, obj: JsonObject = JsonObject()) {
    val properties = obj.asMap().map { (key, element) ->
        key to (element.stringOrNull() ?: element.toString())
    }.toMap()

    data object FirstStart : AnalyticsEvent("first_run")
    class AppUpdated(lastVersion: Int) : AnalyticsEvent("app_updated", jsonObject {
        "last_version" += lastVersion.toString()
    })

    class Navigate(destination: String) : AnalyticsEvent("navigate", jsonObject {
        "destination" += destination
    })
}
