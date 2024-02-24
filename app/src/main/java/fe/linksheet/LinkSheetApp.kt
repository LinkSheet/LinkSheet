package fe.linksheet

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.material.color.DynamicColors
import fe.gson.GlobalGsonContext
import fe.gson.globalGsonModule
import fe.gson.typeadapter.ExtendedTypeAdapter
import fe.linksheet.activity.CrashHandlerActivity
import fe.linksheet.extension.koin.androidApplicationContext
import fe.linksheet.module.analytics.AnalyticsClient
import fe.linksheet.module.analytics.AnalyticsEvent
import fe.linksheet.module.analytics.analyticsModule
import fe.linksheet.module.database.dao.module.daoModule
import fe.linksheet.module.database.databaseModule
import fe.linksheet.module.downloader.downloaderModule
import fe.linksheet.module.lifecycle.processLifecycleCoroutineModule
import fe.linksheet.module.log.AppLogger
import fe.linksheet.module.log.defaultLoggerFactoryModule
import fe.linksheet.module.log.entry.LogEntry
import fe.linksheet.module.log.entry.LogEntryDeserializer
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.preference.featureFlagRepositoryModule
import fe.linksheet.module.preference.preferenceRepositoryModule
import fe.linksheet.module.repository.module.repositoryModule
import fe.linksheet.module.request.requestModule
import fe.linksheet.module.resolver.module.resolverModule
import fe.linksheet.module.resolver.urlresolver.amp2html.amp2HtmlResolveRequestModule
import fe.linksheet.module.resolver.urlresolver.base.allRemoteResolveRequest
import fe.linksheet.module.resolver.urlresolver.cachedRequestModule
import fe.linksheet.module.resolver.urlresolver.redirect.redirectResolveRequestModule
import fe.linksheet.module.viewmodel.module.viewModelModule
import fe.linksheet.shizuku.ShizukuCommand
import fe.linksheet.shizuku.ShizukuHandler
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.Timer
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.lsposed.hiddenapibypass.HiddenApiBypass
import kotlin.system.exitProcess


open class LinkSheetApp : Application(), DefaultLifecycleObserver {
    private lateinit var appLogger: AppLogger
    private lateinit var shizukuHandler: ShizukuHandler<LinkSheetApp>
    private lateinit var timer: Timer

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super<Application>.onCreate()
        timer = Timer.startTimer()

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            val crashIntent = Intent(this, CrashHandlerActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(CrashHandlerActivity.EXTRA_CRASH_EXCEPTION, Log.getStackTraceString(throwable))
            }

            startActivity(crashIntent)
            exitProcess(2)
        }

        AppPreferences.checkDeprecated()

        GlobalGsonContext.configure {
            ExtendedTypeAdapter.Default.register(this)
            registerTypeAdapter(LogEntry::class.java, LogEntryDeserializer)
        }

        if (AndroidVersion.AT_LEAST_API_28_P) {
            HiddenApiBypass.addHiddenApiExemptions("")
        }

        shizukuHandler = ShizukuHandler(this)

        appLogger = AppLogger.createInstance(this)
        appLogger.deleteOldLogs()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        DynamicColors.applyToActivitiesIfAvailable(this)

        val koinApp = startKoin {
            androidLogger()
            androidApplicationContext<LinkSheetApp>(this@LinkSheetApp)
            modules(
                processLifecycleCoroutineModule,
                globalGsonModule,
                preferenceRepositoryModule,
                featureFlagRepositoryModule,
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
                downloaderModule,
                analyticsModule
            )
        }

        if (BuildConfig.DEBUG) {
            // TODO: Remove once user is given the choice to opt in/out
            val analyticsClient = koinApp.koin.get<AnalyticsClient>()
            val preferenceRepository = koinApp.koin.get<AppPreferenceRepository>()

            val lastVersion = preferenceRepository.getInt(AppPreferences.lastVersion)
            analyticsClient.enqueue(createAppStartEvent(lastVersion))
        }
    }


    private fun createAppStartEvent(lastVersion: Int): AnalyticsEvent? {
        return if (lastVersion == -1) AnalyticsEvent.FirstStart
        else if (BuildConfig.VERSION_CODE > lastVersion) AnalyticsEvent.AppUpdated(lastVersion)
        else null
    }

    fun postShizukuCommand(command: ShizukuCommand) {
        shizukuHandler.postShizukuCommand(command)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)

        runCatching {
            val preferenceRepository = get<AppPreferenceRepository>()
            val currentUseTime = preferenceRepository.getLong(AppPreferences.useTimeMs)
            val usedFor = timer.stop()

            preferenceRepository.writeLong(AppPreferences.useTimeMs, currentUseTime + usedFor)
            preferenceRepository.writeInt(AppPreferences.lastVersion, BuildConfig.VERSION_CODE)
        }

        appLogger.writeLog()
    }
}
