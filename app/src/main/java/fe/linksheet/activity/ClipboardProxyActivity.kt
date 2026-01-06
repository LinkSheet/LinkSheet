package fe.linksheet.activity

import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.getSystemService
import fe.linksheet.R
import fe.linksheet.web.UriUtil

class ClipboardProxyActivity : ComponentActivity() {
    
    private var hasHandledClipboard = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !hasHandledClipboard && !isFinishing) {
            hasHandledClipboard = true
            handleClipboard()
        }
    }

    private fun handleClipboard() {
        val clipboardManager = getSystemService<ClipboardManager>()
        if (clipboardManager == null || !clipboardManager.hasPrimaryClip()) {
            showErrorAndFinish()
            return
        }

        val clip = clipboardManager.primaryClip
        if (clip == null || clip.itemCount == 0) {
            showErrorAndFinish()
            return
        }

        val text = clip.getItemAt(0).text?.toString()
        if (text.isNullOrBlank()) {
            showErrorAndFinish()
            return
        }

        val fixedText = if (!text.startsWith("http://") && !text.startsWith("https://") && text.contains(".")) {
           "https://$text"
        } else {
           text
        }
        
        val uri = UriUtil.parseWebUriStrict(fixedText)
        if (uri != null) {
            val intent = Intent(this, BottomSheetActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                data = uri
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } else {
            showErrorAndFinish()
        }
        
        finish()
    }

    private fun showErrorAndFinish() {
        Toast.makeText(this, R.string.qs_tile_no_link, Toast.LENGTH_SHORT).show()
        finish()
    }
}
