package fe.linksheet.util

import app.linksheet.mozilla.components.support.base.log.Log
import app.linksheet.mozilla.components.support.base.log.sink.LogSink

class LinkSheetLogSink(
    private val logsDebug: Boolean = true,
    private val androidLogSink: LogSink,
) : LogSink {

    override fun log(
        priority: Log.Priority,
        tag: String?,
        throwable: Throwable?,
        message: String,
    ) {
        if (priority == Log.Priority.DEBUG && !logsDebug) {
            return
        }

        androidLogSink.log(priority, tag, throwable, message)
    }
}
