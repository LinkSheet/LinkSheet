package fe.linksheet.material3

import android.util.Log
import fe.linksheet.bottom.sheet.BuildConfig

object M3Log {
    private val disabled = true

    fun d(tag: String, msg: String) {
        if (disabled) return
        if (!BuildConfig.DEBUG) return
        Log.d(tag, msg)
    }
}
