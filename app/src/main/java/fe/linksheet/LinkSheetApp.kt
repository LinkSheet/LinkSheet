package fe.linksheet

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.material.color.DynamicColors
import fe.linksheet.activity.CrashHandlerActivity
import fe.linksheet.module.database.dao.daoModule
import fe.linksheet.module.database.databaseModule
import fe.linksheet.module.downloader.downloaderModule
import fe.linksheet.module.log.AppLogger
import fe.linksheet.module.log.loggerFactoryModule
import fe.linksheet.module.preference.preferenceRepositoryModule
import fe.linksheet.module.repository.repositoryModule
import fe.linksheet.module.request.requestModule
import fe.linksheet.module.resolver.redirectResolverModule
import fe.linksheet.module.resolver.resolverModule
import fe.linksheet.module.viewmodel.viewModelModule
import fe.stringbuilder.util.buildSeparatedString
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber
import java.io.BufferedWriter
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDateTime
import kotlin.system.exitProcess


class LinkSheetApp : Application(), DefaultLifecycleObserver {
    private lateinit var appLogger: AppLogger

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super<Application>.onCreate()

        appLogger = AppLogger.createInstance(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        Thread.setDefaultUncaughtExceptionHandler { _, exception ->
            exception.printStackTrace()

            val sw = StringWriter().also { sw ->
                PrintWriter(sw).use { exception.printStackTrace(it) }
            }

            val exceptionText = sw.toString()

            val crashIntent = Intent(this, CrashHandlerActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(CrashHandlerActivity.extraCrashException, exceptionText)
            }

            startActivity(crashIntent)
            exitProcess(2)
        }

        DynamicColors.applyToActivitiesIfAvailable(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@LinkSheetApp)
            modules(
                preferenceRepositoryModule,
                loggerFactoryModule,
                databaseModule,
                daoModule,
                repositoryModule,
                viewModelModule,
                resolverModule,
                requestModule,
                redirectResolverModule,
                downloaderModule
            )
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        appLogger.writeLog()
    }
}