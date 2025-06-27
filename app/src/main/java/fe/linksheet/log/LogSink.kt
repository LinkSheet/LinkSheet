package fe.linksheet.log

interface LogSink {
    fun log(
        level: Log.Level = Log.Level.Debug,
        tag: String? = null,
        throwable: Throwable? = null,
        message: String,
    )
}
