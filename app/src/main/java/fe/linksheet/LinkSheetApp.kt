package fe.linksheet

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.material.color.DynamicColors
import fe.gson.context.GlobalGsonContext
import fe.gson.globalGsonModule
import fe.linksheet.activity.CrashHandlerActivity
import fe.linksheet.extension.koin.androidApplicationContext
import fe.linksheet.extension.koin.applicationLifecycle
import fe.linksheet.lifecycle.ActivityLifecycleObserver
import fe.linksheet.lifecycle.AppLifecycleObserver
import fe.linksheet.module.analytics.AnalyticsClient
import fe.linksheet.module.analytics.AnalyticsEvent
import fe.linksheet.module.analytics.AppStart
import fe.linksheet.module.analytics.analyticsModule
import fe.linksheet.module.analytics.client.DebugLogAnalyticsClient
import fe.linksheet.module.database.dao.module.daoModule
import fe.linksheet.module.database.databaseModule
import fe.linksheet.module.downloader.downloaderModule
import fe.linksheet.module.log.defaultLoggerModule
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.log.file.entry.LogEntryDeserializer
import fe.linksheet.module.log.file.logFileServiceModule
import fe.linksheet.module.network.networkStateServiceModule
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.preferenceRepositoryModule
import fe.linksheet.module.redactor.redactorModule
import fe.linksheet.module.repository.module.repositoryModule
import fe.linksheet.module.request.requestModule
import fe.linksheet.module.resolver.module.resolverModule
import fe.linksheet.module.resolver.urlresolver.amp2html.amp2HtmlResolveRequestModule
import fe.linksheet.module.resolver.urlresolver.base.allRemoteResolveRequest
import fe.linksheet.module.resolver.urlresolver.cachedRequestModule
import fe.linksheet.module.resolver.urlresolver.redirect.redirectResolveRequestModule
import fe.linksheet.module.shizuku.shizukuHandlerModule
import fe.linksheet.module.statistic.statisticsModule
import fe.linksheet.module.viewmodel.module.viewModelModule
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.BuildType
import fe.linksheet.util.HttpUrlTypeAdapter
import fe.linksheet.util.UriTypeAdapter
import kotlinx.coroutines.flow.StateFlow
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.lsposed.hiddenapibypass.HiddenApiBypass
import kotlin.system.exitProcess


class LinkSheetApp : Application() {
    private lateinit var lifecycleObserver: AppLifecycleObserver
    private lateinit var activityLifecycleObserver: ActivityLifecycleObserver

    fun currentActivity(): StateFlow<Activity?> {
        return activityLifecycleObserver.current
    }

    override fun onCreate() {
        super.onCreate()

        activityLifecycleObserver = ActivityLifecycleObserver()
        registerActivityLifecycleCallbacks(activityLifecycleObserver)

        lifecycleObserver = AppLifecycleObserver(ProcessLifecycleOwner.get())
        lifecycleObserver.attach()

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            val crashIntent = Intent(this, CrashHandlerActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(CrashHandlerActivity.EXTRA_CRASH_EXCEPTION, Log.getStackTraceString(throwable))
            }

            startActivity(crashIntent)
            exitProcess(2)
        }

        GlobalGsonContext.configure {
            registerTypeAdapter(LogEntry::class.java, LogEntryDeserializer)
            UriTypeAdapter.register(this)
            HttpUrlTypeAdapter.register(this)
        }

        if (AndroidVersion.AT_LEAST_API_28_P) {
            HiddenApiBypass.addHiddenApiExemptions("")
        }

        DynamicColors.applyToActivitiesIfAvailable(this)

        val koinApplication = startKoin {
            androidLogger()
            androidApplicationContext<LinkSheetApp>(this@LinkSheetApp)
            applicationLifecycle(lifecycleObserver)
            modules(
                networkStateServiceModule,
                logFileServiceModule,
                shizukuHandlerModule,
                globalGsonModule,
                preferenceRepositoryModule,
                redactorModule,
                defaultLoggerModule,
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
                if (BuildType.current.allowDebug) analyticsModule else DebugLogAnalyticsClient.module,
                statisticsModule
            )
        }

        lifecycleObserver.dispatchAppInitialized()

        if (BuildType.current.allowDebug) {
            // TODO: Remove once user is given the choice to opt in/out
            val analyticsClient = koinApplication.koin.get<AnalyticsClient>()
            val preferenceRepository = koinApplication.koin.get<AppPreferenceRepository>()

            val lastVersion = preferenceRepository.get(AppPreferences.lastVersion)
            analyticsClient.enqueue(createAppStartEvent(lastVersion))
        }
    }

    private fun createAppStartEvent(lastVersion: Int): AnalyticsEvent {
        return if (lastVersion == -1) AppStart.FirstRun
        else if (BuildConfig.VERSION_CODE > lastVersion) AppStart.Updated(lastVersion)
        else AppStart.Default
    }
}
