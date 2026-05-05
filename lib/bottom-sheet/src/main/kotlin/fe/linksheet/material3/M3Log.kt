package fe.linksheet.material3

import app.linksheet.mozilla.components.support.base.log.logger.Logger

object M3Log {
    private val logger = Logger("M3Log")
    private var enabled = false

    fun d(tag: String, msg: String) {
        if (!enabled) return
        logger.debug(msg)
    }

    fun setEnabled(it: Boolean) {
        this.enabled = it
    }
}
