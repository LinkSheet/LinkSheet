package fe.linksheet.extension

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import fe.gson.dsl.jsonObject
import fe.gson.extension.mapToJsonObjectArray


fun Throwable.toAllCauseStackTraceJsonArray(maxFrameCount: Int = 50): JsonArray {
    var count = 0

    val array = JsonArray()
    for (throwable in getAllCauses()) {
        val stackTraceSize = throwable.stackTrace.size
        if (count + stackTraceSize >= maxFrameCount) break

        count += stackTraceSize
        array.add(throwable.toStacktraceJson())
    }

    return array
}

fun Throwable.toStacktraceJson(): JsonObject {
    return jsonObject {
        "type" += toString().substringBefore(':').substringBeforeLast('.')
        "value" += message
        "frames" += stackTrace.mapToJsonObjectArray {
            "class" += it.className
            "method" += it.methodName
            "isNative" += it.isNativeMethod
            "line" += it.lineNumber
            "file" += it.fileName
        }
    }
}

private fun Throwable.getAllCauses(): List<Throwable> {
    val cache = mutableSetOf<Throwable>()

    var current: Throwable? = this
    while (current != null && cache.add(current)) {
        current = current.cause
    }

    return cache.reversed()
}
