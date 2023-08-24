package fe.linksheet.interconnect

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * A collection of utilities for interacting with
 * LinkSheet from external clients.
 */
object LinkSheet {
    const val PACKAGE_NAME = "fe.linksheet"
    const val NIGHTLY_PACKAGE = "$PACKAGE_NAME.nightly"
    const val DEBUG_PACKAGE = "$PACKAGE_NAME.debug"

    const val INTERCONNECT_COMPONENT = "fe.linksheet.InterconnectService"

    /**
     * Check if LinkSheet (release or debug) is installed.
     */
    fun Context.isLinkSheetInstalled(): Boolean {
        return getInstalledPackageName() != null
    }

    /**
     * Get the installed package name, if any.
     * If multiple LinkSheet versions are installed (release/nightly/debug), this will return
     * release > nightly > debug
     * If LinkSheet is not installed, this returns null.
     */
    fun Context.getInstalledPackageName(): String? {
        val pkgs = listOf(PACKAGE_NAME, NIGHTLY_PACKAGE, DEBUG_PACKAGE)

        return pkgs.firstOrNull {
            try {
                @Suppress("DEPRECATION")
                packageManager.getApplicationInfo(it, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    fun Context.supportsInterconnect(): Boolean {
        val installedPackage = getInstalledPackageName() ?: return false

        return try {
            @Suppress("DEPRECATION")
            packageManager.getServiceInfo(ComponentName(installedPackage, INTERCONNECT_COMPONENT), 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * A convenience function for binding the interconnect service.
     *
     * If multiple LinkSheet versions are installed (release/nightly/debug), this will bind to
     * release > nightly > debug
     */
    fun Context.bindService(onBound: (LinkSheetServiceConnection) -> Unit) {
        if (!isLinkSheetInstalled()) {
            throw IllegalStateException("LinkSheet is not installed!")
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            `package` = getInstalledPackageName()
            component = ComponentName(`package`!!, INTERCONNECT_COMPONENT)
        }

        val connection = object : LinkSheetServiceConnection() {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                this.service = ILinkSheetService.Stub.asInterface(service)
                onBound(this)
            }

            override fun disconnect() {
                unbindService(this)
            }
        }

        ContextCompat.startForegroundService(this, intent,)
        bindService(intent, connection, Context.BIND_AUTO_CREATE,)
    }

    /**
     * A convenience function for binding the interconnect service.
     *
     * If multiple LinkSheet versions are installed (release/nightly/debug), this will bind to
     * release > nightly > debug
     */
    suspend fun Context.bindService(): LinkSheetServiceConnection {
        return suspendCoroutine { continuation ->
            bindService {
                continuation.resume(it)
            }
        }
    }
}
