package fe.linksheet

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.work.WorkManager
import app.linksheet.api.DependencyProvider
import app.linksheet.compose.debug.DebugMenuSlotProvider
import app.linksheet.compose.debug.DebugPreferenceProvider
import app.linksheet.compose.debug.NoOpDebugMenuSlotProvider
import app.linksheet.compose.debug.NoOpDebugPreferenceProvider
import app.linksheet.feature.browser.PrivateBrowsingModule
import app.linksheet.feature.downloader.DownloaderModule
import app.linksheet.feature.engine.LinkEngineFeatureModule
import app.linksheet.feature.libredirect.LibRedirectFeatureModule
import app.linksheet.feature.scenario.ScenarioModule
import app.linksheet.feature.shizuku.ShizukuModule
import app.linksheet.lib.log.AndroidLogSink
import app.linksheet.lib.log.LLog
import app.linksheet.testing.Testing
import com.google.android.material.color.DynamicColors
import fe.android.lifecycle.CurrentActivityObserver
import fe.android.lifecycle.ProcessServiceRegistry
import fe.android.lifecycle.koin.extension.applicationLifecycle
import fe.composekit.core.AndroidVersion
import fe.composekit.lifecycle.network.koin.NetworkStateServiceModule
import fe.droidkit.koin.androidApplicationContext
import fe.gson.GlobalGsonModule
import fe.gson.context.GlobalGsonContext
import fe.linksheet.activity.CrashHandlerActivity
import fe.linksheet.module.analytics.AnalyticsServiceModule
import fe.linksheet.module.analytics.client.DebugLogAnalyticsClient
import fe.linksheet.feature.app.AppFeatureModule
import fe.linksheet.module.clock.ClockModule
import fe.linksheet.module.database.DatabaseModule
import fe.linksheet.module.database.dao.module.DaoModule
import fe.linksheet.module.devicecompat.CompatModule
import fe.linksheet.module.devicecompat.miui.MiuiCompatProvider
import fe.linksheet.module.devicecompat.miui.RealMiuiCompatProvider
import fe.linksheet.module.devicecompat.oneui.OneUiCompatProvider
import fe.linksheet.module.devicecompat.oneui.RealOneUiCompatProvider
import fe.linksheet.module.http.HttpModule
import fe.linksheet.module.language.AppLocaleModule
import fe.linksheet.module.log.DefaultLogModule
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.log.file.entry.LogEntryDeserializer
import fe.linksheet.module.paste.PasteServiceModule
import fe.linksheet.module.preference.PreferenceRepositoryModule
import fe.linksheet.module.preference.state.AppStateServiceModule
import fe.linksheet.feature.profile.ProfileFeatureModule
import fe.linksheet.module.receiver.BroadcastEventBusModule
import fe.linksheet.module.remoteconfig.RemoteConfigClientModule
import fe.linksheet.module.repository.module.RepositoryModule
import fe.linksheet.module.resolver.module.ResolverModule
import fe.linksheet.module.resolver.urlresolver.UrlResolverModule
import fe.linksheet.module.shizuku.ShizukuServiceModule
import fe.linksheet.module.statistic.StatisticsModule
import fe.linksheet.module.systeminfo.SystemInfoServiceModule
import fe.linksheet.module.versiontracker.VersionTrackerModule
import fe.linksheet.module.viewmodel.module.ViewModelModule
import fe.linksheet.module.workmanager.WorkDelegatorServiceModule
import fe.linksheet.util.serialization.HttpUrlTypeAdapter
import fe.linksheet.util.serialization.UriTypeAdapter
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
            PrivateBrowsingModule,
            AppFeatureModule,
            AppLocaleModule,
            NetworkStateServiceModule,
            ShizukuServiceModule,
            GlobalGsonModule,
            PreferenceRepositoryModule,
            DefaultLogModule,
            provideCompatProvider(),
            CompatModule,
            DatabaseModule,
            DaoModule,
            RepositoryModule,
            HttpModule,
            DownloaderModule,
            RemoteConfigClientModule,
            UrlResolverModule,
            ResolverModule,
            ViewModelModule,
            provideAnalyticsClient(),
            AnalyticsServiceModule,
            StatisticsModule,
            VersionTrackerModule,
            PasteServiceModule,
            ProfileFeatureModule,
            AppStateServiceModule,
            provideDebugModule(),
            WorkDelegatorServiceModule,
            BroadcastEventBusModule,
            ShizukuModule,
            ScenarioModule,
            LibRedirectFeatureModule,
            LinkEngineFeatureModule
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
