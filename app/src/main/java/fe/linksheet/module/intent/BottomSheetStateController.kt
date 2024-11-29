@file:OptIn(ExperimentalMaterial3Api::class)

package fe.linksheet.module.intent

import android.app.Activity
import android.content.Intent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.fix.SheetState
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface BottomSheetStateController {
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
    val coroutineScope: CoroutineScope,
    val drawerState: SheetState,
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
