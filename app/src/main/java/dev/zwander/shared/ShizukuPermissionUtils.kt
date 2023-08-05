package dev.zwander.shared

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider

object ShizukuPermissionUtils {
    @Composable
    fun rememberHasShizukuPermissionAsState(): State<Boolean> {
        val hasPermission = remember {
            mutableStateOf(Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED)
        }

        DisposableEffect(null) {
            val listener = Shizuku.OnRequestPermissionResultListener { _, grantResult ->
                hasPermission.value = grantResult == PackageManager.PERMISSION_GRANTED
            }

            Shizuku.addRequestPermissionResultListener(listener)

            onDispose {
                Shizuku.removeRequestPermissionResultListener(listener)
            }
        }

        return hasPermission
    }

    fun isShizukuRunning(): Boolean {
        return Shizuku.pingBinder()
    }

    fun Context.isShizukuInstalled(): Boolean {
        return kotlin.runCatching {
            packageManager.getPackageInfo(
                ShizukuProvider.MANAGER_APPLICATION_ID,
                0
            )
        }.getOrNull() != null
    }
}
