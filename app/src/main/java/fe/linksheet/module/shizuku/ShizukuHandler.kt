package fe.linksheet.module.shizuku

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import dev.zwander.shared.IShizukuService
import dev.zwander.shared.shizuku.ShizukuService
import fe.linksheet.BuildConfig
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import rikka.shizuku.Shizuku
import java.util.*


val shizukuHandlerModule = module {
    singleOf(::ShizukuHandler)
}

data class ShizukuCommand<T>(val command: IShizukuService.() -> T, val resultHandler: (T) -> Unit) {
    fun run(userService: IShizukuService) {
        resultHandler(command(userService))
    }
}

class ShizukuHandler(val context: Context) : ServiceConnection, Shizuku.OnRequestPermissionResultListener {
    private val queuedCommands: Queue<ShizukuCommand<*>> = LinkedList()
    private var userService: IShizukuService? = null

    private val serviceArgs = Shizuku.UserServiceArgs(ComponentName(context, ShizukuService::class.java))
        .version(BuildConfig.VERSION_CODE + (if (BuildConfig.DEBUG) 9999 else 0))
        .processNameSuffix("shizuku")
        .debuggable(BuildConfig.DEBUG)
        .daemon(false)
        .tag("${context.packageName}_shizuku")

    init {
        Shizuku.addBinderReceivedListenerSticky {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) rebindUserService()
            else Shizuku.addRequestPermissionResultListener(this)
        }
    }

    fun <T> enqueueCommand(command: ShizukuCommand<T>) {
        if (userService != null) {
            command.run(userService!!)
        } else queuedCommands.add(command)
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
            queuedCommands.poll()?.run(userService!!)
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
