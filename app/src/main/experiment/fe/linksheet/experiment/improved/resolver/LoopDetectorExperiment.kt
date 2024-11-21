package fe.linksheet.experiment.improved.resolver

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import fe.linksheet.R
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.extension.android.showToast

class LoopDetectorExperiment(
    private val activity: BottomSheetActivity,
) : ActivityResultContract<Intent, ActivityResult>() {

    private val launcher = activity.registerForActivityResult(this) {
        Log.d(BottomSheetActivity::class.simpleName, "Received result for $it")
        if (it.resultCode == Activity.RESULT_OK) {
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
            launcher.launch(intent)
            return true
        } catch (e: ActivityNotFoundException) {
            return false
        }
    }

    override fun createIntent(context: Context, input: Intent): Intent = input

    override fun parseResult(
        resultCode: Int,
        intent: Intent?,
    ): ActivityResult = ActivityResult(resultCode, intent)
}

