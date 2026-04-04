package fe.linksheet.module.log.file.entry

import androidx.annotation.Keep
import com.google.gson.JsonObject
import fe.gson.dsl.jsonObject
import fe.kotlin.extension.string.decodeBase64Throw
import fe.kotlin.extension.string.encodeBase64Throw
import fe.std.javatime.extension.unixMillisUtc
import fe.stringbuilder.util.buildSeparatedString
import kotlinx.serialization.Serializable
import java.time.format.DateTimeFormatter
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class SerializableLogEntry(
    val type: String,
    val time: String,
    val prefix: String,
    val message: String,
)

@OptIn(ExperimentalEncodingApi::class)
@Keep
sealed class LogEntry(
    val type: String,
    val unixMillis: Long,
    val prefix: String? = null,
    val message: String,
    private val redactedMessage: String? = null,
) {
    override fun toString() = buildSeparatedString(" ") {
        item(prefix = null) { append(type) }
        item(prefix = null) { append(unixMillis) }
        itemNotNull(prefix, prefix = null) { append(prefix) }
        item(prefix = null) { append(message.encodeBase64Throw()) }
        itemNotNull(redactedMessage, prefix = null) { append(redactedMessage!!.encodeBase64Throw()) }
    }

    fun toSerializable(redact: Boolean, includeThrowable: Boolean): SerializableLogEntry {
        val throwableNotIncluded = !includeThrowable && this@LogEntry is FatalEntry
        val isRedacted = redact && this@LogEntry is DefaultLogEntry
        return SerializableLogEntry(
            type = type,
            time = unixMillis.unixMillisUtc.value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            prefix = prefix ?: "<none>",
            message = when {
                throwableNotIncluded -> "<not_included>"
                isRedacted -> redactedMessage ?: "<no_message>"
                else -> message
            }
        )
    }

    fun toCopyLogJson(redact: Boolean, includeThrowable: Boolean): JsonObject {
        val throwableNotIncluded = !includeThrowable && this@LogEntry is FatalEntry
        val isRedacted = redact && this@LogEntry is DefaultLogEntry

        return jsonObject {
            "type" += type
            "time" += unixMillis.unixMillisUtc.value
            if (prefix != null) {
                "prefix" += prefix
            }

            if (throwableNotIncluded) {
                "message" += "<not_included>"
            } else if (isRedacted) {
                "redactedMessage" += redactedMessage
            } else {
                "message" += message
            }
        }
    }

    companion object {
        fun fromLogFileLine(line: String): DefaultLogEntry? {
            val splitLine = line.split(" ")
            if (splitLine.size != 5) return null

            val (type, unixMillis, prefix, message, redactedMessage) = splitLine
            return DefaultLogEntry(
                type,
                unixMillis.toLong(),
                prefix,
                message.decodeBase64Throw(),
                redactedMessage.decodeBase64Throw()
            )
        }
    }

    @Keep
    class DefaultLogEntry(
        type: String,
        unixMillis: Long = System.currentTimeMillis(),
        prefix: String,
        message: String,
        redactedMessage: String? = null,
    ) : LogEntry(type, unixMillis, prefix, message, redactedMessage)

    @Keep
    class FatalEntry(
        unixMillis: Long = System.currentTimeMillis(),
        message: String,
    ) : LogEntry("Crash", unixMillis, message = message)
}
