package fe.linksheet.activity.bottomsheet

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import fe.linksheet.R
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.extension.android.showToast
import kotlinx.coroutines.flow.MutableStateFlow

class LoopDetector(
    private val activity: BottomSheetActivity,
) : ActivityResultContract<Intent, ActivityResult>() {
    private var initialIntent = MutableStateFlow<Intent?>(null)
    private var latestNewIntent = MutableStateFlow<Intent?>(null)

    private val launcher = activity.registerForActivityResult(this) {
        Log.d(BottomSheetActivity::class.simpleName, "Received result for $it")
        handleResult(it)
    }

    private fun handleResult(result: ActivityResult) {
        // Apps may "refuse" to handle an intent and return back to LinkSheet instantly
        // * Amazon does this for Prime links, which results in a new intent being passed to onNewIntent
        // (and subsequently being written to latestNewIntent) and RESULT_CANCELED
        // * Hermit also sends RESULT_CANCELED for some reason, but doesn't provide a new intent first, meaning we can
        // still differentiate between a successful and a non-successful launch using the condition below
        if (result.resultCode == Activity.RESULT_OK || latestNewIntent.value == null) {
            activity.finish()
        } else {
            activity.showToast(
                textId = R.string.bottom_sheet__event_app_refusal,
                duration = Toast.LENGTH_LONG,
                uiThread = true
            )
        }
    }

    fun start(intent: Intent): Boolean {
        try {
            launcher.launch(intent, ActivityOptionsCompat.makeBasic())
            return true
        } catch (e: ActivityNotFoundException) {
            return false
        }
    }

    override fun createIntent(context: Context, input: Intent): Intent {
        return input
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?,
    ): ActivityResult {
        return ActivityResult(resultCode, intent)
    }

    suspend fun onNewIntent(intent: Intent) {
        latestNewIntent.emit(intent)
    }

    suspend fun setInitialIntent(intent: Intent) {
        initialIntent.emit(intent)
        latestNewIntent.emit(null)
    }
}
