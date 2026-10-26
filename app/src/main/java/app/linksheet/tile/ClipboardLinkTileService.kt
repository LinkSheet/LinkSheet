package app.linksheet.tile

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import app.linksheet.activity.ClipboardProxyActivity
import fe.composekit.core.AndroidVersion

/**
 * Known issue: TileService might cause memory leaks on some Android versions.
 * See: https://github.com/square/leakcanary/issues/2207
 * See: https://issuetracker.google.com/issues/410432415
 * After extensive exploration, this appears to be a system-level issue that cannot be resolved here.
 */
class ClipboardLinkTileService : TileService() {

    @SuppressLint("StartActivityAndCollapseDeprecated")
    override fun onClick() {
        super.onClick()

        // Android 10+ restricts background clipboard access.
        // We must start a foreground activity to read it.
        val intent = Intent(this, ClipboardProxyActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (AndroidVersion.isAtLeastApi34U()) {
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
