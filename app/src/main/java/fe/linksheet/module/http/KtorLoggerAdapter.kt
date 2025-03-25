package fe.linksheet.module.http

import io.ktor.client.plugins.logging.Logger

import fe.linksheet.module.log.Logger as LinkSheetLogger

class KtorLoggerAdapter(private val logger: LinkSheetLogger) : Logger {
    override fun log(message: String) {
        logger.verbose(message)
    }
}
