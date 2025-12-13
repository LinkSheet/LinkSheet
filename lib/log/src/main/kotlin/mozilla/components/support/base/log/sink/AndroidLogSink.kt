/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.support.base.log.sink

import mozilla.components.support.base.ext.getStacktraceAsString
import mozilla.components.support.base.log.Log

/**
 * <code>LogSink</code> implementation that writes to Android's log.
 *
 * @param defaultTag A default tag that should be used for all logging calls without tag.
 */
class AndroidLogSink(
    private val defaultTag: String = "App",
) : LogSink {
    /**
     * Low-level logging call.
     */
    override fun log(priority: Log.Priority, tag: String?, throwable: Throwable?, message: String) {
        val logTag = tag(tag)

        val logMessage: String = if (throwable != null) {
            "$message\n${throwable.getStacktraceAsString()}"
        } else {
            message
        }

        android.util.Log.println(priority.value, logTag, logMessage)
    }

    private fun tag(candidate: String?): String = candidate ?: defaultTag
}
