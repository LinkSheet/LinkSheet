package app.linksheet.feature.shizuku.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.getApplicationInfoCompatOrNull
import app.linksheet.api.eventbus.BroadcastEventBus
import app.linksheet.api.eventbus.IntentEventHandler
import fe.composekit.intent.buildIntent
import fe.linksheet.util.ApplicationInfoFlags
import fe.linksheet.util.IntentFilters
import fe.std.coroutines.RefreshableStateFlow
import fe.std.coroutines.asStateFlow
import fe.std.result.getOrNull
import fe.std.result.tryCatch
import kotlinx.coroutines.flow.update
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider

@Suppress("FunctionName")
internal fun AndroidShizukuService(
    eventBus: BroadcastEventBus,
    packageManager: PackageManager,
): ShizukuService {
    val service = ShizukuService(
        getApplicationInfoOrNull = packageManager::getApplicationInfoCompatOrNull,
        pingBinder = Shizuku::pingBinder,
        checkSelfPermission = {
            tryCatch { Shizuku.checkSelfPermission() }.getOrNull()
        },
        requestPermission = {
            tryCatch { Shizuku.requestPermission(it) }
        }
    )
    Shizuku.addRequestPermissionResultListener(service)
    eventBus.register(service)

    return service
}

class ShizukuService(
    private val getApplicationInfoOrNull: (String, ApplicationInfoFlags) -> ApplicationInfo?,
    private val pingBinder: () -> Boolean,
    private val checkSelfPermission: () -> Int?,
    private val requestPermission: (Int) -> Unit
) : Shizuku.OnRequestPermissionResultListener,
    Shizuku.OnBinderReceivedListener,
    Shizuku.OnBinderDeadListener,
    IntentEventHandler {

    companion object {
        private const val REQUEST_CODE = 10000

        val ManagerIntent = buildIntent(
            action = Intent.ACTION_VIEW,
            componentName = ComponentName(ShizukuProvider.MANAGER_APPLICATION_ID, "moe.shizuku.manager.MainActivity")
        )
    }

    private val _statusFlow = RefreshableStateFlow(ShizukuStatus.Unknown) {
        val installed = getApplicationInfoOrNull(ShizukuProvider.MANAGER_APPLICATION_ID, ApplicationInfoFlags.EMPTY) != null
        val running = pingBinder()
        val permission = if (running) checkSelfPermission() == PackageManager.PERMISSION_GRANTED else false

        ShizukuStatus(
            installed = installed,
            running = running,
            permission = permission
        )
    }
    val statusFlow = _statusFlow.asStateFlow()

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (requestCode == REQUEST_CODE) {
            _statusFlow.update { it.copy(permission = grantResult == PackageManager.PERMISSION_GRANTED) }
        }
    }

    fun requestPermission() {
        requestPermission(REQUEST_CODE)
    }

    fun isShizukuRunning(): Boolean {
        return pingBinder()
    }

    override val filter = IntentFilters.packageState

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.data?.schemeSpecificPart == ShizukuProvider.MANAGER_APPLICATION_ID) {
            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED -> _statusFlow.update { it.copy(installed = true) }
                Intent.ACTION_PACKAGE_REMOVED -> _statusFlow.update { it.copy(installed = false) }
            }
        } else {

        }
    }

    override fun onBinderReceived() {
        _statusFlow.update { it.copy(running = true) }
    }

    override fun onBinderDead() {
        _statusFlow.update { it.copy(running = false) }
    }
}

data class ShizukuStatus(
    val installed: Boolean,
    val running: Boolean,
    val permission: Boolean
) {
    companion object {
        val Unknown = ShizukuStatus(installed = false, running = false, permission = false)
    }

    val allOk = installed && permission && running
}
