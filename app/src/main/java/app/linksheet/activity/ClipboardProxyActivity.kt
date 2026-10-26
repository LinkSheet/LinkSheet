package app.linksheet.activity

import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.lifecycle.lifecycleScope
import fe.linksheet.R
import fe.linksheet.module.viewmodel.ClipboardProxyException
import fe.linksheet.module.viewmodel.ClipboardProxyViewModel
import fe.linksheet.util.intent.StandardIntents
import fe.std.result.isFailure
import fe.std.result.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.time.Duration.Companion.milliseconds

class ClipboardProxyActivity : ComponentActivity() {
    private val viewModel by viewModel<ClipboardProxyViewModel>()
    private var hasHandledClipboard = false

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus || hasHandledClipboard || isFinishing) return
        hasHandledClipboard = true
        val result = viewModel.handleClipboard()
        if (result.isFailure()) {
            when (result.exception) {
                is ClipboardProxyException.NoText -> {
                    showToastAndFinish(R.string.qs_tile_no_link)
                }
                else -> {
                    showToastAndFinish(R.string.qs_tile_failed_to_parse_links)
                }
            }
        }
        if (result.isSuccess()) {
            val intent = StandardIntents.createSelfIntent(result.value)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
    private fun showToastAndFinish(@StringRes resId: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(this@ClipboardProxyActivity, resId, Toast.LENGTH_LONG).show()
            delay(Toast.LENGTH_LONG.milliseconds)
            finish()
        }
    }
}
