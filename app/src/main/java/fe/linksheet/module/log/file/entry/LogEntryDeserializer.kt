package fe.linksheet.module.log.file.entry

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import fe.gson.extension.json.element.`object`
import fe.gson.extension.json.`object`.asLong
import fe.gson.extension.json.`object`.asLongOrNull
import fe.gson.extension.json.`object`.asString
import fe.gson.extension.json.`object`.asStringOrNull
import java.lang.reflect.Type

object LogEntryDeserializer : JsonDeserializer<LogEntry> {
    override fun deserialize(element: JsonElement, member: Type, context: JsonDeserializationContext): LogEntry? {
        return runCatching {
            val obj = element.`object`()

            // Fallback to minified field names present in some builds where entries where not exempt from minification
            val type = obj.asStringOrNull("type") ?: obj.asString("a")
            val time = obj.asLongOrNull("unixMillis") ?: obj.asLong("b")
            val message = obj.asStringOrNull("message") ?: obj.asString("c")
            val prefix = (obj.asStringOrNull("prefix") ?: obj.asStringOrNull("d"))
                ?: return LogEntry.FatalEntry(time, message)

            val redactedMessage = obj.asStringOrNull("redactedMessage") ?: obj.asString("e")
            return LogEntry.DefaultLogEntry(type, time, prefix, message, redactedMessage)
        }.getOrNull()
    }
}
