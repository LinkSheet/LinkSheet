package fe.linksheet.module.analytics

import com.google.gson.JsonObject
import fe.gson.dsl.jsonObject

sealed class AnalyticsEvent(val name: String, obj: JsonObject = JsonObject()) {
    val properties = obj.toString()

    data object FirstStart : AnalyticsEvent("first_run")
    class AppUpdated(lastVersion: Int) : AnalyticsEvent("app_updated", jsonObject {
        "last_version" += lastVersion.toString()
    })
}
