package app.linksheet

import android.util.Log

object M3Log {
    private var enabled = false

    fun d(tag: String, msg: String) {
        if (!enabled) return
        Log.d(tag, msg)
    }

    fun d(msg: String) {
        if (!enabled) return
        Log.d("M3Log", msg)
    }

    fun setEnabled(it: Boolean) {
        this.enabled = it
    }
}
