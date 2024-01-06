package fe.linksheet.shizuku

import android.app.Application
import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import dev.zwander.shared.IShizukuService
import dev.zwander.shared.shizuku.ShizukuService
import fe.linksheet.BuildConfig
import rikka.shizuku.Shizuku
import java.util.LinkedList
import java.util.Queue

typealias ShizukuCommand = IShizukuService.() -> Unit

class ShizukuHandler<T : Application>(
    val app: T,
) : ServiceConnection, Shizuku.OnRequestPermissionResultListener {
    private val queuedCommands: Queue<ShizukuCommand> = LinkedList()
    private var userService: IShizukuService? = null

    private val serviceArgs = Shizuku.UserServiceArgs(ComponentName(app, ShizukuService::class.java))
        .version(BuildConfig.VERSION_CODE + (if (BuildConfig.DEBUG) 9999 else 0))
        .processNameSuffix("shizuku")
        .debuggable(BuildConfig.DEBUG)
        .daemon(false)
        .tag("${app.packageName}_shizuku")

    init {
        Shizuku.addBinderReceivedListenerSticky {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) rebindUserService()
            else Shizuku.addRequestPermissionResultListener(this)
        }
    }

    fun postShizukuCommand(command: ShizukuCommand) {
        if (userService != null) command(userService!!)
        else queuedCommands.add(command)
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            rebindUserService()
            Shizuku.removeRequestPermissionResultListener(this)
        }
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        userService = IShizukuService.Stub.asInterface(service)
        while (queuedCommands.isNotEmpty() && userService != null) {
            queuedCommands.poll()?.invoke(userService!!)
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        userService = null
    }

    override fun onBindingDied(name: ComponentName) {
        userService = null
    }

    private fun rebindUserService() {
        Shizuku.unbindUserService(serviceArgs, this, true)
        Shizuku.bindUserService(serviceArgs, this)
    }
}
