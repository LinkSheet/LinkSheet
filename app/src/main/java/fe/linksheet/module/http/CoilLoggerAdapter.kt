package fe.linksheet.module.http

import coil3.util.Logger
import mozilla.components.support.base.log.logger.Logger as MozLogger


class CoilLoggerAdapter(
    override var minLevel: Logger.Level = Logger.Level.Verbose
) : Logger {
    private val logger = MozLogger("Coil")

    override fun log(
        tag: String,
        level: Logger.Level,
        message: String?,
        throwable: Throwable?
    ) {
        when (level) {
            Logger.Level.Verbose -> logger.info("$tag $message", throwable)
            Logger.Level.Debug -> logger.debug("$tag $message", throwable)
            Logger.Level.Info -> logger.info("$tag $message", throwable)
            Logger.Level.Warn -> logger.warn("$tag $message", throwable)
            Logger.Level.Error -> logger.error("$tag $message", throwable)
        }
    }
}
