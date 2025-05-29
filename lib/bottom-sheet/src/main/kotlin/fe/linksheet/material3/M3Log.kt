package fe.linksheet.material3

import android.util.Log
import fe.linksheet.bottom.sheet.BuildConfig

object M3Log {

    fun d(tag: String, msg: String) {
        if (!BuildConfig.DEBUG) return
        Log.d(tag, msg)
    }
}
