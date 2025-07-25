package fe.linksheet

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.work.WorkManager
import app.linksheet.testing.Testing
import com.google.android.material.color.DynamicColors
import fe.android.lifecycle.CurrentActivityObserver
import fe.android.lifecycle.ProcessServiceRegistry
import fe.android.lifecycle.koin.extension.applicationLifecycle
import fe.composekit.core.AndroidVersion
import fe.droidkit.koin.androidApplicationContext
import fe.gson.GlobalGsonModule
import fe.gson.context.GlobalGsonContext
import fe.linksheet.activity.CrashHandlerActivity
import fe.linksheet.log.AndroidLogSink
import fe.linksheet.log.LLog
import fe.linksheet.module.analytics.AnalyticsServiceModule
import fe.linksheet.module.analytics.client.DebugLogAnalyticsClient
import fe.linksheet.module.app.PackageModule
import fe.linksheet.module.clock.ClockModule
import fe.linksheet.module.database.dao.module.DaoModule
import fe.linksheet.module.database.DatabaseModule
import fe.linksheet.module.debug.DebugMenuSlotProvider
import fe.linksheet.module.debug.DebugPreferenceProvider
import fe.linksheet.module.debug.NoOpDebugMenuSlotProvider
import fe.linksheet.module.debug.NoOpDebugPreferenceProvider
import fe.linksheet.module.devicecompat.CompatModule
import fe.linksheet.module.devicecompat.miui.MiuiCompatProvider
import fe.linksheet.module.devicecompat.miui.RealMiuiCompatProvider
import fe.linksheet.module.devicecompat.oneui.OneUiCompatProvider
import fe.linksheet.module.devicecompat.oneui.RealOneUiCompatProvider
import fe.linksheet.module.downloader.DownloaderModule
import fe.linksheet.module.language.AppLocaleModule
import fe.linksheet.module.http.HttpModule
import fe.linksheet.module.log.DefaultLogModule
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.log.file.entry.LogEntryDeserializer
import fe.linksheet.module.paste.PasteServiceModule
import fe.linksheet.module.preference.PreferenceRepositoryModule
import fe.linksheet.module.preference.state.AppStateServiceModule
import fe.linksheet.module.profile.ProfileSwitcherModule
import fe.linksheet.module.remoteconfig.RemoteConfigClientModule
import fe.linksheet.module.repository.module.RepositoryModule
import fe.linksheet.module.resolver.module.ResolverModule
import fe.linksheet.module.resolver.urlresolver.UrlResolverModule
import fe.linksheet.module.shizuku.ShizukuHandlerModule
import fe.linksheet.module.statistic.StatisticsModule
import fe.linksheet.module.systeminfo.SystemInfoServiceModule
import fe.linksheet.module.versiontracker.VersionTrackerModule
import fe.linksheet.module.viewmodel.module.ViewModelModule
import fe.linksheet.module.workmanager.WorkDelegatorServiceModule
import fe.linksheet.util.serialization.HttpUrlTypeAdapter
import fe.linksheet.util.serialization.UriTypeAdapter
import fe.composekit.lifecycle.network.koin.NetworkStateServiceModule
import kotlinx.coroutines.flow.StateFlow
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.time.LocalDateTime
import kotlin.system.exitProcess


open class LinkSheetApp : Application(), DependencyProvider {
    val startupTime: LocalDateTime = LocalDateTime.now()

    private val currentActivityObserver = CurrentActivityObserver()
    private val lifecycleObserver by lazy { ProcessServiceRegistry() }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(currentActivityObserver)
        LLog.addSink(AndroidLogSink())

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            val crashIntent = Intent(this, CrashHandlerActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(CrashHandlerActivity.EXTRA_CRASH_EXCEPTION, Log.getStackTraceString(throwable))
                .putExtra(CrashHandlerActivity.EXTRA_CRASH_TIMESTAMP, System.currentTimeMillis())

            startActivity(crashIntent)
            exitProcess(2)
        }

        GlobalGsonContext.configure {
            registerTypeAdapter(LogEntry::class.java, LogEntryDeserializer)
            UriTypeAdapter.register(this)
            HttpUrlTypeAdapter.register(this)
        }

        if (AndroidVersion.isAtLeastApi28P() && !Testing.IsTestRunner) {
            HiddenApiBypass.addHiddenApiExemptions("")
        }

        DynamicColors.applyToActivitiesIfAvailable(this)

        val koinModules = provideKoinModules()
        val koinApplication = startKoin {
            androidLogger()
            androidApplicationContext(this@LinkSheetApp)
            applicationLifecycle(lifecycleObserver)
            if (!WorkManager.isInitialized()) {
                workManagerFactory()
            }
            modules(koinModules)
        }

        lifecycleObserver.onAppInitialized()
    }

    override fun provideKoinModules(): List<Module> {
        return listOf(
            ClockModule,
            SystemInfoServiceModule,
            PackageModule,
            AppLocaleModule,
            NetworkStateServiceModule,
            ShizukuHandlerModule,
            GlobalGsonModule,
            PreferenceRepositoryModule,
            DefaultLogModule,
            provideCompatProvider(),
            CompatModule,
            DatabaseModule,
            DaoModule,
            RepositoryModule,
            HttpModule,
            RemoteConfigClientModule,
            UrlResolverModule,
            ResolverModule,
            ViewModelModule,
            DownloaderModule,
            provideAnalyticsClient(),
            AnalyticsServiceModule,
            StatisticsModule,
            VersionTrackerModule,
            PasteServiceModule,
            ProfileSwitcherModule,
            AppStateServiceModule,
            provideDebugModule(),
            WorkDelegatorServiceModule
        )
    }

    override fun provideCompatProvider(): Module {
        return module {
            single<MiuiCompatProvider> { RealMiuiCompatProvider(get()) }
            single<OneUiCompatProvider> { RealOneUiCompatProvider(get()) }
        }
    }

    override fun provideAnalyticsClient(): Module {
        return DebugLogAnalyticsClient.module
    }

    override fun provideDebugModule(): Module {
        return module {
            single<DebugMenuSlotProvider> { NoOpDebugMenuSlotProvider }
            single<DebugPreferenceProvider> { NoOpDebugPreferenceProvider }
        }
    }

    fun currentActivity(): StateFlow<Activity?> {
        return currentActivityObserver.current
    }
}
