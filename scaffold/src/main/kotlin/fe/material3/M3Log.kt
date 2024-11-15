package fe.material3

import android.util.Log

object M3Log {
    private const val ENABLED = false

    fun d(tag: String, msg: String) {
        if (!ENABLED) return
        Log.d(tag, msg)
    }
}
