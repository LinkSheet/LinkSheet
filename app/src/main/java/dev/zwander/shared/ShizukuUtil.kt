package dev.zwander.shared

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.*
import fe.linksheet.R
import fe.linksheet.extension.android.getApplicationInfoCompat
import fe.linksheet.extension.android.startActivityWithConfirmation
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    suspend fun requestPermission(): Boolean {
        return suspendCoroutine { cont ->
            val listener = object : Shizuku.OnRequestPermissionResultListener {
                override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                    Shizuku.removeRequestPermissionResultListener(this)
                    cont.resume(grantResult == PackageManager.PERMISSION_GRANTED)
                }
            }

            Shizuku.addRequestPermissionResultListener(listener)
            Shizuku.requestPermission(100)
        }
    }

    fun startManager(activity: Activity) {
        val success = activity.startActivityWithConfirmation(Intent(Intent.ACTION_VIEW).apply {
            component = MANAGER_COMPONENT
        })

        if (!success) {
            Toast.makeText(activity, R.string.shizuku_manager_start_failed, Toast.LENGTH_LONG).show()
        }
    }
}
