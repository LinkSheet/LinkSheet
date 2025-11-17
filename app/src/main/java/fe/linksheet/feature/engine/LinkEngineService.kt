package fe.linksheet.feature.engine

import android.app.Service
import android.content.Intent
import android.os.IBinder
import fe.linksheet.module.resolver.IntentResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.android.inject

class LinkEngineService : Service(), CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val intentResolver by inject<IntentResolver>()

    override fun onCreate() {
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return 1
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
