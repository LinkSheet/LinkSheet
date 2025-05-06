package fe.linksheet.composable.page.settings.advanced.exportimport

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
fun rememberFileSelectedLauncher(onResult: (FileSelectionResult) -> Unit): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val fileSelectedLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { onResult(it.toResult()) }
    )

    return fileSelectedLauncher
}

sealed interface FileSelectionResult {
    data class Ok(val uri: Uri) : FileSelectionResult
    data object Cancelled : FileSelectionResult
}

fun ActivityResult.toResult(): FileSelectionResult {
    return when (resultCode) {
        Activity.RESULT_OK if data?.data != null -> FileSelectionResult.Ok(data?.data!!)
        else -> FileSelectionResult.Cancelled
    }
}
