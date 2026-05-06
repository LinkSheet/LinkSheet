package fe.linksheet.activity.bottomsheet.compat

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.os.HandlerCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// Adapted from https://cs.android.com/androidx/platform/frameworks/support/+/3f70bff82ac09fc9d95e093dfa86845d2f4e98b4:compose/ui/ui/src/androidMain/kotlin/androidx/compose/ui/platform/WindowRecomposer.android.kt;bpv=0
class SettingsObserver<T>(
    private val applicationContext: Context,
    private val settingName: String,
    private val getValue: (Context, String) -> T,
    private val getUri: (String) -> Uri,
    private val handler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())
) {
    private val uri = getUri(settingName)
    private val resolver = applicationContext.contentResolver

    fun readValue() = getValue(applicationContext, settingName)

    fun createFlow(): Flow<T> = callbackFlow {
        val callback = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                trySendBlocking(readValue())
            }
        }
        resolver.registerContentObserver(uri, false, callback)

        awaitClose {
            resolver.unregisterContentObserver(callback)
        }
    }
}
