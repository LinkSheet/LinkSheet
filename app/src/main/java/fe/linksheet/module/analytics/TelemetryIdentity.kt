package fe.linksheet.module.analytics

import com.google.gson.JsonObject
import fe.gson.dsl.JsonObjectDslInit
import fe.gson.dsl.new

open class TelemetryIdentity(obj: JsonObject = JsonObject()) {
    private val identity = obj.toString()

    constructor(init: JsonObjectDslInit) : this(init.new())

    companion object Anonymous : TelemetryIdentity()

    fun createEvent(event: AnalyticsEvent): Map<String, String> {
        return mapOf("properties" to event.properties, "identity" to identity)
    }
}
