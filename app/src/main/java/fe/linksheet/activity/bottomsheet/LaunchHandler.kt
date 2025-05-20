package fe.linksheet.activity.bottomsheet

import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.AndroidRuntimeException
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat

class LaunchHandler(private val launcher: ActivityResultLauncher<Intent>) {
    fun start(intent: Intent): LaunchResult {
        try {
            launcher.launch(intent, ActivityOptionsCompat.makeBasic())
            return LaunchResult.Success
        } catch (e: Exception) {
            return handleException(e)
        }
    }

    fun handleException(ex: Exception): LaunchResult {
        return when (ex) {
            is ActivityNotFoundException -> LaunchResult.NotFound(ex)
            is SecurityException -> LaunchResult.NotAllowed(ex)
            is AndroidRuntimeException -> LaunchResult.Other(ex)
            is IllegalArgumentException -> LaunchResult.Illegal(ex)
            is IllegalStateException -> LaunchResult.Illegal(ex)
            else -> LaunchResult.Unknown(ex)
        }
    }
}

sealed interface LaunchResult {
    data object Success : LaunchResult
    class NotFound(ex: ActivityNotFoundException) : LaunchFailure(ex)
    class NotAllowed(ex: SecurityException) : LaunchFailure(ex)
    class Illegal(ex: Exception) : LaunchFailure(ex)
    class Other(ex: AndroidRuntimeException) : LaunchFailure(ex)
    class Unknown(ex: Exception) : LaunchFailure(ex)
}

sealed class LaunchFailure(val ex: Exception) : LaunchResult {

}
