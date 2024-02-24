package fe.linksheet.module.analytics

import com.google.gson.JsonObject
import fe.gson.dsl.JsonObjectDslInit
import fe.gson.dsl.new
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

open class TelemetryIdentity(obj: JsonObject = JsonObject()) {
    @OptIn(ExperimentalEncodingApi::class)
    private val identity = Base64.encode(obj.toString().toByteArray())

    constructor(init: JsonObjectDslInit) : this(init.new())

    companion object Anonymous : TelemetryIdentity()

    fun createEvent(event: AnalyticsEvent): Map<String, String> {
        return event.properties
//        return mapOf("properties" to event.properties
//            , "identity" to identity
//        )
    }
}
