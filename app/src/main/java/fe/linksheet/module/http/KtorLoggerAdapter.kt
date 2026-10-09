package fe.linksheet.module.http

import io.ktor.client.plugins.logging.*
import fe.composekit.mozilla.components.support.base.log.logger.Logger as MozLogger

object KtorLoggerAdapter : Logger {
    private val logger = MozLogger("Ktor")

    override fun log(message: String) {
        logger.info(message)
    }
}
