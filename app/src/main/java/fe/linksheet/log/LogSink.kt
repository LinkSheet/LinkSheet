package fe.linksheet.log

interface LogSink {
    fun log(
        level: LLog.Level = LLog.Level.Debug,
        tag: String? = null,
        throwable: Throwable? = null,
        message: String,
    )
}
