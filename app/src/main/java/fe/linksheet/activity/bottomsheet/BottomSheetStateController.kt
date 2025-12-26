@file:OptIn(ExperimentalMaterial3Api::class)

package fe.linksheet.activity.bottomsheet

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.ExperimentalMaterial3Api
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.Job

interface BottomSheetStateController {
    val editorLauncher: ActivityResultLauncher<Intent>

    val dispatch: (BottomSheetInteraction) -> Unit
}


fun Activity.hideAndFinish(hideSheet: () -> Job) {
    val job = hideSheet()
    val onCompletion: CompletionHandler? = { finish() }
    if (onCompletion != null) {
        job.invokeOnCompletion(onCompletion)
    }
}

class DefaultBottomSheetStateController(
    override val editorLauncher: ActivityResultLauncher<Intent>,
    override val dispatch: (BottomSheetInteraction) -> Unit,
) : BottomSheetStateController
