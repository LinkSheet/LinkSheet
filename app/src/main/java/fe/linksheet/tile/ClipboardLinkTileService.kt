package fe.linksheet.tile

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import androidx.annotation.RequiresApi
import fe.linksheet.R
import fe.linksheet.activity.ClipboardProxyActivity
import android.app.PendingIntent

@RequiresApi(Build.VERSION_CODES.N)
class ClipboardLinkTileService : TileService() {

    override fun onClick() {
        super.onClick()
        
        // Android 10+ restricts background clipboard access.
        // We must start a foreground activity to read it.
        val intent = Intent(this, ClipboardProxyActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (Build.VERSION.SDK_INT >= 34) {
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            startActivityAndCollapse(pendingIntent)
        } else {
            startActivityAndCollapse(intent)
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        qsTile?.state = Tile.STATE_INACTIVE
        qsTile?.updateTile()
    }
}
