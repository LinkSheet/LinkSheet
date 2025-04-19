@file:OptIn(ExperimentalMaterial3Api::class)

package fe.linksheet.activity.bottomsheet

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.ExperimentalMaterial3Api
import fe.linksheet.activity.bottomsheet.compat.CompatSheetState
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface BottomSheetStateController {
    val editorLauncher: ActivityResultLauncher<Intent>
    val onNewIntent: (Intent) -> Unit

    fun hideAndFinish()

    fun hide(onCompletion: CompletionHandler? = null)

    fun startActivity(intent: Intent)

    fun finish()

    fun createChooser(intent: Intent): Intent {
        return intent
    }
}

class DefaultBottomSheetStateController(
    val activity: Activity,
    override val editorLauncher: ActivityResultLauncher<Intent>,
    val coroutineScope: CoroutineScope,
    val drawerState: CompatSheetState,
    override val onNewIntent: (Intent) -> Unit
) : BottomSheetStateController {

    override fun hideAndFinish() {
        hide { finish() }
    }

    override fun hide(onCompletion: CompletionHandler?) {
        val job = coroutineScope.launch { drawerState.hide() }
        if (onCompletion != null) {
            job.invokeOnCompletion(onCompletion)
        }
    }

    override fun startActivity(intent: Intent) {
        activity.startActivity(intent)
    }

    override fun finish() {
        activity.finish()
    }

    override fun createChooser(intent: Intent): Intent {
        return Intent.createChooser(intent, null)
    }
}
