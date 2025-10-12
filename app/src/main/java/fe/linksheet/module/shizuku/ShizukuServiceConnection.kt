@file:Suppress("FunctionName")

package fe.linksheet.module.shizuku

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import dev.zwander.shared.IShizukuService
import dev.zwander.shared.shizuku.ShizukuService
import fe.linksheet.BuildConfig
import org.koin.dsl.module
import rikka.shizuku.Shizuku
import java.util.*


val ShizukuServiceModule = module {
    single {
        ShizukuServiceConnection(context = get())
    }
}

data class ShizukuCommand<T>(
    val command: IShizukuService.() -> T,
    val resultHandler: (T) -> Unit
)


class ShizukuServiceConnection(
    context: Context,
) : ServiceConnection, Shizuku.OnRequestPermissionResultListener {
    private val queuedCommands: Queue<ShizukuCommand<*>> = LinkedList()
    private var userService: IShizukuService? = null

    private val serviceArgs = Shizuku.UserServiceArgs(ComponentName(context, ShizukuService::class.java))
        .version(BuildConfig.VERSION_CODE + (if (BuildConfig.DEBUG) 9999 else 0))
        .processNameSuffix("shizuku")
        .debuggable(BuildConfig.DEBUG)
        .daemon(false)
        .tag("${BuildConfig.APPLICATION_ID}_shizuku")

    init {
        addBinderReceivedListener()
    }

    private fun <T> ShizukuCommand<T>.run(userService: IShizukuService) {
        val result = command(userService)
        resultHandler(result)
    }

    fun <T> enqueueCommand(command: ShizukuCommand<T>) {
        userService?.let { command.run(it) } ?: queuedCommands.add(command)
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        checkPermission(grantResult)
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        val userService = IShizukuService.Stub.asInterface(service)
        this.userService = userService
        while (queuedCommands.isNotEmpty()) {
            queuedCommands.poll()?.run(userService)
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

    private fun addBinderReceivedListener() {
        Shizuku.addBinderReceivedListenerSticky {
            val grantResult = Shizuku.checkSelfPermission()
            checkPermission(grantResult)
        }
    }

    private fun checkPermission(grantResult: Int) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            rebindUserService()
            Shizuku.removeRequestPermissionResultListener(this)
        } else {
            Shizuku.addRequestPermissionResultListener(this)
        }
    }
}
