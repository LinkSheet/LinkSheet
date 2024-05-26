package fe.linksheet.experiment.ui.overhaul.composable.page.settings.debug.log

import fe.linksheet.module.log.file.entry.LogEntry

data class PrefixMessageCardContent(
    val type: String,
    val prefix: String?,
    val start: Long,
    val messages: MutableList<String> = mutableListOf()
) {
    fun matches(logEntry: LogEntry): Boolean {
        return prefix == logEntry.prefix
    }

    fun add(entry: LogEntry) {
        val diffMs = entry.unixMillis - start
        val s = diffMs / 1000
        val ms = (diffMs % 1000).toInt()

        val diff = "%02d.%03dS".format(s, ms)
        messages.add("${entry.type}+$diff: ${entry.message}")
//        messages.add("${entry.type}(T+$diff): ${entry.message}")
    }
}
