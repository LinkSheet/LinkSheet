package fe.linksheet.module.http

import coil3.util.Logger

class CoilLoggerAdapter(
    val logger: fe.linksheet.module.log.Logger,
    override var minLevel: Logger.Level = Logger.Level.Verbose
) : Logger {
    override fun log(
        tag: String,
        level: Logger.Level,
        message: String?,
        throwable: Throwable?
    ) {
        when (level) {
            Logger.Level.Verbose -> logger.verbose("$tag $message", throwable)
            Logger.Level.Debug -> logger.debug("$tag $message", throwable)
            Logger.Level.Info -> logger.info("$tag $message", throwable)
            Logger.Level.Warn -> logger.info("$tag $message", throwable)
            Logger.Level.Error -> logger.error("$tag $message", throwable)
        }
    }
}
