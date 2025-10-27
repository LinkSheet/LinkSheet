package app.linksheet.lib.log

class PrintLogSink : LogSink {
    override fun log(
        level: LLog.Level,
        tag: String?,
        throwable: Throwable?,
        message: String
    ) {
        val printMessage = buildString {
            append("${level.name[0]} ")
            if (tag != null) {
                append("[$tag] ")
            }
            append(message)
        }
        println(printMessage)
        throwable?.printStackTrace()
    }
}
