package app.linksheet.activity

import android.content.ClipboardManager
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.getSystemService
import fe.composekit.extension.getFirstText
import fe.linksheet.R
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.util.intent.parser.IntentParser
import fe.std.result.getOrNull

class ClipboardProxyActivity : ComponentActivity() {

    private var hasHandledClipboard = false

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !hasHandledClipboard && !isFinishing) {
            hasHandledClipboard = true
            handleClipboard()
        }
    }

    private fun handleClipboard() {
        val text = getSystemService<ClipboardManager>()?.getFirstText()
        val uri = text?.let(IntentParser::parseText)?.getOrNull()

        if (uri != null) {
            val intent = Intent(this, BottomSheetActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                data = uri
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, R.string.qs_tile_no_link, Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
