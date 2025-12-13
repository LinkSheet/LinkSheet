package fe.linksheet.module.http

import io.ktor.client.plugins.logging.*
import mozilla.components.support.base.log.logger.Logger as MozLogger

class KtorLoggerAdapter(private val logger: MozLogger) : Logger {
    override fun log(message: String) {
        logger.info(message)
    }
}
