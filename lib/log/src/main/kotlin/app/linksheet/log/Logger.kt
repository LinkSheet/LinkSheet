package app.linksheet.log

import app.linksheet.mozilla.components.support.base.log.logger.Logger

inline fun <reified T> createLogger(): Logger {
    return Logger(T::class.simpleName ?: "<empty>")
}
