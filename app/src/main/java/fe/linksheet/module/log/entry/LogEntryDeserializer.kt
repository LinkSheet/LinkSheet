package fe.linksheet.module.log.entry

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import fe.gson.extension.json.element.`object`
import fe.gson.extension.json.`object`.asLong
import fe.gson.extension.json.`object`.asString
import fe.gson.extension.json.`object`.asStringOrNull
import java.lang.reflect.Type

object LogEntryDeserializer : JsonDeserializer<LogEntry> {
    override fun deserialize(element: JsonElement, member: Type, context: JsonDeserializationContext): LogEntry? {
        return runCatching {
            val obj = element.`object`()

            val type = obj.asString("type")
            val time = obj.asLong("unixMillis")
            val message = obj.asString("message")
            val prefix = obj.asStringOrNull("prefix") ?: return LogEntry.FatalEntry(time, message)

            val redactedMessage = obj.asString("redactedMessage")
            return LogEntry.DefaultLogEntry(type, time, prefix, message, redactedMessage)
        }.getOrNull()
    }
}
