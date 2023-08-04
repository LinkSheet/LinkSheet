package fe.linksheet

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.material.color.DynamicColors
import dev.zwander.shared.shizuku.ShizukuService
import fe.kotlin.extension.asString
import fe.linksheet.activity.CrashHandlerActivity
import fe.linksheet.module.database.dao.module.daoModule
import fe.linksheet.module.database.databaseModule
import fe.linksheet.module.downloader.downloaderModule
import fe.linksheet.module.log.AppLogger
import fe.linksheet.module.log.defaultLoggerFactoryModule
import fe.linksheet.module.preference.preferenceRepositoryModule
import fe.linksheet.module.repository.module.repositoryModule
import fe.linksheet.module.request.requestModule
import fe.linksheet.module.resolver.module.resolverModule
import fe.linksheet.module.resolver.urlresolver.amp2html.amp2HtmlResolveRequestModule
import fe.linksheet.module.resolver.urlresolver.base.allRemoteResolveRequest
import fe.linksheet.module.resolver.urlresolver.cachedRequestModule
import fe.linksheet.module.resolver.urlresolver.redirect.redirectResolveRequestModule
import fe.linksheet.module.viewmodel.module.viewModelModule
import fe.linksheet.util.AndroidVersion
import moe.shizuku.server.IShizukuService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.Shizuku
import kotlin.system.exitProcess


class LinkSheetApp : Application(), DefaultLifecycleObserver {
    private lateinit var appLogger: AppLogger

    private var userService: IShizukuService? = null

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            userService = IShizukuService.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            userService = null
        }
    }

    private val serviceArgs by lazy {
        Shizuku.UserServiceArgs(
            ComponentName(this, ShizukuService::class.java)
        )
            .version(BuildConfig.VERSION_CODE + (if (BuildConfig.DEBUG) 9999 else 0))
            .processNameSuffix("shizuku")
            .debuggable(BuildConfig.DEBUG)
            .daemon(false)
            .tag("${packageName}_shizuku")
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super<Application>.onCreate()

        if (AndroidVersion.AT_LEAST_API_28_P) {
            HiddenApiBypass.addHiddenApiExemptions("")
        }

        appLogger = AppLogger.createInstance(this)
        appLogger.deleteOldLogs()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        Thread.setDefaultUncaughtExceptionHandler { _, exception ->
            val crashIntent = Intent(this, CrashHandlerActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(CrashHandlerActivity.extraCrashException, exception.asString())
            }

            startActivity(crashIntent)
            exitProcess(2)
        }

        DynamicColors.applyToActivitiesIfAvailable(this)

        startKoin {
            androidLogger()
            androidContext(this@LinkSheetApp)
            modules(
                preferenceRepositoryModule,
                defaultLoggerFactoryModule,
                databaseModule,
                daoModule,
                cachedRequestModule,
                redirectResolveRequestModule,
                amp2HtmlResolveRequestModule,
                allRemoteResolveRequest,
                resolverModule,
                repositoryModule,
                viewModelModule,
                requestModule,
                downloaderModule
            )
        }

        Shizuku.addBinderReceivedListener {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                addUserService()
            } else {
                Shizuku.addRequestPermissionResultListener(
                    object : Shizuku.OnRequestPermissionResultListener {
                        override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                                addUserService()
                                Shizuku.removeRequestPermissionResultListener(this)
                            }
                        }
                    }
                )
            }
        }
    }

    private fun addUserService() {
        Shizuku.unbindUserService(
            serviceArgs,
            userServiceConnection,
            true
        )

        Shizuku.bindUserService(
            serviceArgs,
            userServiceConnection,
        )
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        appLogger.writeLog()
    }
}