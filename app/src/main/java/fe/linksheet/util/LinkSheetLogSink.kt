package fe.linksheet.util

import fe.composekit.mozilla.components.support.base.log.Log
import fe.composekit.mozilla.components.support.base.log.sink.LogSink

class LinkSheetLogSink(
    private var logsDebug: Boolean = true,
    private var enabled: Boolean = true,
    private val delegateToSink: LogSink,
) : LogSink {

    init {
        linkSheetSinks.add(this)
    }

    companion object {
        private val linkSheetSinks = mutableListOf<LinkSheetLogSink>()

        fun setEnabled(enabled: Boolean) {
            linkSheetSinks.forEach { it.enabled = enabled }
        }
    }


    override fun log(
        priority: Log.Priority,
        tag: String?,
        throwable: Throwable?,
        message: String,
    ) {
        if (!enabled) return
        if (priority == Log.Priority.DEBUG && !logsDebug) return

        delegateToSink.log(priority, tag, throwable, message)
    }
}
