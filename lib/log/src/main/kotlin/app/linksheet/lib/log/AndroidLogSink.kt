package app.linksheet.lib.log

import android.util.Log
import fe.kotlin.extension.asString

class AndroidLogSink : LogSink {
    override fun log(
        level: LLog.Level,
        tag: String?,
        throwable: Throwable?,
        message: String
    ) {
        val logMessage: String = if (throwable != null) {
            "$message\n${throwable.asString()}"
        } else {
            message
        }

        Log.println(level.value, tag, logMessage)
    }
}
