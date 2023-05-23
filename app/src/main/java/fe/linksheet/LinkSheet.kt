package fe.linksheet

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.material.color.DynamicColors
import fe.linksheet.activity.CrashHandlerActivity
import fe.linksheet.module.database.dao.daoModule
import fe.linksheet.module.database.databaseModule
import fe.linksheet.module.downloader.downloaderModule
import fe.linksheet.module.log.loggerFactoryModule
import fe.linksheet.module.preference.preferenceRepositoryModule
import fe.linksheet.module.repository.repositoryModule
import fe.linksheet.module.request.requestModule
import fe.linksheet.module.resolver.redirectResolverModule
import fe.linksheet.module.resolver.resolverModule
import fe.linksheet.module.viewmodel.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess


class LinkSheet : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { _, exception ->
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
            androidContext(this@LinkSheet)
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
}