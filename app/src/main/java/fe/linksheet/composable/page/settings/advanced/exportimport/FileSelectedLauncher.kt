package fe.linksheet.composable.page.settings.advanced.exportimport

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import kotlinx.parcelize.Parcelize

@Composable
fun rememberFileSelectedLauncher(onResult: (FileSelectionResult) -> Unit): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val fileSelectedLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { onResult(it.toResult()) }
    )

    return fileSelectedLauncher
}

sealed interface FileSelectionResult {
    @Parcelize
    data class Ok(val uri: Uri) : FileSelectionResult, Parcelable
    data object Cancelled : FileSelectionResult
}

fun ActivityResult.toResult(): FileSelectionResult {
    return when (resultCode) {
        Activity.RESULT_OK if data?.data != null -> FileSelectionResult.Ok(data?.data!!)
        else -> FileSelectionResult.Cancelled
    }
}
