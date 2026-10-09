package app.linksheet.feature.shizuku.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.getApplicationInfoCompatOrNull
import android.os.IBinder
import app.linksheet.api.eventbus.BroadcastEventBus
import app.linksheet.api.eventbus.IntentEventHandler
import app.linksheet.feature.shizuku.IShizukuUserService
import fe.composekit.flag.ApplicationInfoFlags
import fe.composekit.intent.buildIntent
import fe.composekit.log.createLogger
import fe.composekit.mozilla.components.support.base.log.logger.Logger
import fe.linksheet.util.IntentFilters
import fe.std.coroutines.RefreshableStateFlow
import fe.std.coroutines.asStateFlow
import fe.std.result.getOrNull
import fe.std.result.ifFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider

@Suppress("FunctionName")
internal fun AndroidShizukuService(
    eventBus: BroadcastEventBus,
    packageManager: PackageManager,
    config: UserServiceConfig,
): ShizukuService {
    val service = ShizukuService(
        config = config,
        getApplicationInfoOrNull = packageManager::getApplicationInfoCompatOrNull,
        wrappedShizuku = RealWrappedShizuku,
        managerComponent = ComponentName(
            ShizukuProvider.MANAGER_APPLICATION_ID,
            "moe.shizuku.manager.MainActivity"
        )
    )
    Shizuku.addBinderReceivedListenerSticky(service)
    Shizuku.addBinderDeadListener(service)
    Shizuku.addRequestPermissionResultListener(service)
    eventBus.register(service)

    return service
}

class ShizukuService(
    config: UserServiceConfig,
    private val getApplicationInfoOrNull: (String, ApplicationInfoFlags) -> ApplicationInfo?,
    private val wrappedShizuku: WrappedShizuku,
    private val managerComponent: ComponentName,
) : Shizuku.OnRequestPermissionResultListener,
    Shizuku.OnBinderReceivedListener,
    Shizuku.OnBinderDeadListener,
    ServiceConnection,
    IntentEventHandler {

    private val logger = Logger("ShizukuService")
    private val serviceArgs = Shizuku.UserServiceArgs(
        ComponentName(
            config.packageName,
            ShizukuUserService::class.java.name
        )
    )
        .version(config.versionCode)
        .processNameSuffix("shizuku")
        .debuggable(config.debuggable)
        .daemon(false)
        .tag("${config.tag}_shizuku")

    val managerIntent = buildIntent(
        action = Intent.ACTION_VIEW,
        componentName = managerComponent
    )

    companion object {
        private const val REQUEST_CODE = 10000
    }

    private val _statusFlow = RefreshableStateFlow(ShizukuStatus.Unknown, ::computeShizukuStatus)
    val statusFlow = _statusFlow.asStateFlow()

    private val _userServiceFlow = MutableStateFlow<IShizukuUserService?>(null)
    val userServiceFlow = _userServiceFlow.asStateFlow()

    private suspend fun computeShizukuStatus(): ShizukuStatus {
        val installed = getApplicationInfoOrNull(managerComponent.packageName, ApplicationInfoFlags.EMPTY) != null
        if (!installed) {
            return ShizukuStatus.Unknown
        }

        val running = isShizukuRunning()
        return ShizukuStatus(
            installed = true,
            running = running,
            permission = if (running) checkPermission() else false
        )
    }

    fun requestPermission() {
        wrappedShizuku.requestPermission(REQUEST_CODE)
    }

    fun isShizukuRunning(): Boolean {
        return wrappedShizuku.pingBinder().getOrNull() == true
    }

    private fun checkPermission(): Boolean {
        return wrappedShizuku.checkSelfPermission().getOrNull() == PackageManager.PERMISSION_GRANTED
    }

    private fun rebind() {
        val unbindResult = wrappedShizuku.unbindUserService(serviceArgs, this)
        logger.debug("unbind result", unbindResult.ifFailure()?.exception)
        val bindResult = wrappedShizuku.bindUserService(serviceArgs, this)
        logger.debug("bind result", bindResult.ifFailure()?.exception)
    }

    //<editor-fold desc="ServiceConnection">
    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        logger.debug("onServiceConnected")
        val userService = IShizukuUserService.Stub.asInterface(service)
        _userServiceFlow.tryEmit(userService)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        logger.debug("onServiceDisconnected")
        _userServiceFlow.tryEmit(null)
    }

    override fun onBindingDied(name: ComponentName) {
        logger.debug("onBindingDied")
        _userServiceFlow.tryEmit(null)
    }
    //</editor-fold>

    //<editor-fold desc="IntentEventHandler">
    override val filter = IntentFilters.packageState
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.data?.schemeSpecificPart != managerComponent.packageName) return
        val newStatus = when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> ShizukuStatus.Unknown.copy(installed = true)
            Intent.ACTION_PACKAGE_REMOVED -> ShizukuStatus.Unknown
            else -> null
        }
        newStatus?.let {
            _statusFlow.value = it
        }
    }
    //</editor-fold>

    //<editor-fold desc="OnBinderReceivedListener">
    override fun onBinderReceived() {
        logger.debug("onBinderReceived")
        val permission = checkPermission()
        if (permission) {
            val result = rebind()
        }
        _statusFlow.update { it.copy(running = true, permission = permission) }
    }
    //</editor-fold>

    //<editor-fold desc="OnBinderDeadListener">
    override fun onBinderDead() {
        logger.debug("onBinderDead")
        _statusFlow.update { it.copy(running = false) }
    }
    //</editor-fold>

    //<editor-fold desc="OnRequestPermissionResultListener">
    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (requestCode != REQUEST_CODE) return

        val granted = grantResult == PackageManager.PERMISSION_GRANTED
        logger.debug("onRequestPermissionResult, granted: $granted")
        _statusFlow.update { it.copy(permission = granted) }
        logger.debug("trying to rebind")
        rebind()
    }
    //</editor-fold>
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
