package dev.zwander.shared

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import fe.linksheet.extension.android.getApplicationInfoCompat
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider

object ShizukuUtil {
    val MANAGER_COMPONENT = ComponentName(ShizukuProvider.MANAGER_APPLICATION_ID, "moe.shizuku.manager.MainActivity")

    @Composable
    fun rememberHasShizukuPermissionAsState(): State<Boolean> {
        val hasPermission = remember {
            mutableStateOf(Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED)
        }

        DisposableEffect(Unit) {
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

    fun isShizukuInstalled(context: Context): Boolean {
        return context.packageManager.getApplicationInfoCompat(ShizukuProvider.MANAGER_APPLICATION_ID, 0) != null
    }
}
