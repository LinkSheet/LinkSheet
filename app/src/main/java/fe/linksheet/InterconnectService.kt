package fe.linksheet

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.ServiceCompat

class InterconnectService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        return null
    }
}
