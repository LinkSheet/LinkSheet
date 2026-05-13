package fe.linksheet

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import app.linksheet.api.DependencyProvider
import app.linksheet.compose.debug.DebugMenuSlotProvider
import app.linksheet.compose.debug.DebugPreferenceProvider
import app.linksheet.compose.debug.NoOpDebugMenuSlotProvider
import app.linksheet.compose.debug.NoOpDebugPreferenceProvider
import app.linksheet.feature.analytics.client.DebugLogAnalyticsClient
import app.linksheet.feature.analytics.service.AnalyticsServiceModule
import app.linksheet.feature.app.AppModule
import app.linksheet.feature.browser.PrivateBrowsingModule
import app.linksheet.feature.devicecompat.CompatModule
import app.linksheet.feature.devicecompat.miui.MiuiCompatProvider
import app.linksheet.feature.devicecompat.miui.RealMiuiCompatProvider
import app.linksheet.feature.devicecompat.oneui.OneUiCompatProvider
import app.linksheet.feature.devicecompat.oneui.RealOneUiCompatProvider
import app.linksheet.feature.downloader.DownloaderFeatureModule
import app.linksheet.feature.engine.LinkEngineFeatureModule
import app.linksheet.feature.libredirect.LibRedirectFeatureModule
import app.linksheet.feature.libredirect.LibRedirectMigratorModule
import app.linksheet.feature.profile.ProfileFeatureModule
import app.linksheet.feature.remoteconfig.RemoteConfigFeatureModule
import app.linksheet.feature.shizuku.ShizukuModule
import app.linksheet.feature.wiki.WikiFeatureModule
import app.linksheet.mozilla.components.support.base.log.Log
import app.linksheet.mozilla.components.support.base.log.sink.AndroidLogSink
import app.linksheet.testing.Testing
import app.linksheet.util.buildconfig.StaticBuildInfo
import com.google.android.material.color.DynamicColors
import fe.android.lifecycle.CurrentActivityObserver
import fe.android.lifecycle.ProcessServiceRegistry
import fe.android.lifecycle.koin.extension.applicationLifecycle
import fe.composekit.core.AndroidVersion
import fe.composekit.lifecycle.network.koin.NetworkStateServiceModule
import fe.composekit.route.NavTypes
import fe.droidkit.koin.androidApplicationContext
import fe.gson.GlobalGsonModule
import fe.gson.context.GlobalGsonContext
import fe.linksheet.activity.CrashHandlerActivity
import fe.linksheet.module.clock.ClockModule
import fe.linksheet.module.database.DatabaseModule
import fe.linksheet.module.http.HttpModule
import fe.linksheet.module.language.AppLocaleModule
import fe.linksheet.module.log.DefaultLogModule
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.log.file.entry.LogEntryDeserializer
import fe.linksheet.module.paste.PasteServiceModule
import fe.linksheet.module.preference.PreferenceRepositoryModule
import fe.linksheet.module.preference.state.AppStateServiceModule
import fe.linksheet.module.receiver.BroadcastEventBusModule
import fe.linksheet.module.refine.RefineModule
import fe.linksheet.module.repository.module.RepositoryModule
import fe.linksheet.module.resolver.module.ResolverModule
import fe.linksheet.module.resolver.urlresolver.UrlResolverModule
import fe.linksheet.module.shizuku.ShizukuServiceModule
import fe.linksheet.module.statistic.StatisticsModule
import fe.linksheet.module.systeminfo.SystemInfoServiceModule
import fe.linksheet.module.versiontracker.VersionTrackerModule
import fe.linksheet.module.viewmodel.module.ViewModelModule
import fe.linksheet.util.LinkSheetLogSink
import fe.linksheet.util.serialization.HttpUrlTypeAdapter
import fe.linksheet.util.serialization.UriTypeAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
    protected val owner by lazy { ProcessLifecycleOwner.get() }
    private val lifecycleObserver by lazy { ProcessServiceRegistry(owner) }

    override fun onCreate() {
        super.onCreate()
        owner.lifecycleScope.launch(Dispatchers.IO) {
            NavTypes.Types
        }
        StaticBuildInfo.init(BuildConfig.DEBUG, BuildConfig.BUILD_TYPE)
        registerActivityLifecycleCallbacks(currentActivityObserver)
        Log.addSink(LinkSheetLogSink(logsDebug = StaticBuildInfo.IsDebug, AndroidLogSink()))

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            CrashHandlerActivity.start(this, throwable)
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
        startKoin {
            allowOverride(StaticBuildInfo.IsDebug)
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
            RefineModule,
            ClockModule,
            SystemInfoServiceModule,
            PrivateBrowsingModule,
            provideAppModule(),
            AppLocaleModule,
            NetworkStateServiceModule,
            ShizukuServiceModule,
            GlobalGsonModule,
            PreferenceRepositoryModule,
            DefaultLogModule,
            provideCompatProvider(),
            CompatModule,
            LibRedirectMigratorModule,
            DatabaseModule,
            RepositoryModule,
            HttpModule,
            DownloaderFeatureModule,
            RemoteConfigFeatureModule,
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
            BroadcastEventBusModule,
            ShizukuModule,
            LibRedirectFeatureModule,
            LinkEngineFeatureModule,
            WikiFeatureModule,
            PrivateBrowsingModule,
        )
    }

    override fun provideCompatProvider(): Module {
        return module {
            single<MiuiCompatProvider> { RealMiuiCompatProvider(get(), get()) }
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

    override fun provideAppModule(): Module = AppModule

    fun currentActivity(): StateFlow<Activity?> {
        return currentActivityObserver.current
    }
}
