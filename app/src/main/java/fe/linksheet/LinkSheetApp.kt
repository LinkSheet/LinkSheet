package fe.linksheet

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import app.linksheet.testing.Testing
import com.google.android.material.color.DynamicColors
import fe.android.lifecycle.CurrentActivityObserver
import fe.android.lifecycle.ProcessServiceRegistry
import fe.android.lifecycle.koin.extension.applicationLifecycle
import fe.android.version.AndroidVersion
import fe.droidkit.koin.androidApplicationContext
import fe.gson.context.GlobalGsonContext
import fe.gson.globalGsonModule
import fe.linksheet.activity.CrashHandlerActivity
import fe.linksheet.module.analytics.analyticsServiceModule
import fe.linksheet.module.analytics.client.DebugLogAnalyticsClient
import fe.linksheet.module.app.PackageModule
import fe.linksheet.module.database.dao.module.daoModule
import fe.linksheet.module.database.databaseModule
import fe.linksheet.module.debug.DebugMenuSlotProvider
import fe.linksheet.module.debug.DebugPreferenceProvider
import fe.linksheet.module.debug.NoOpDebugMenuSlotProvider
import fe.linksheet.module.debug.NoOpDebugPreferenceProvider
import fe.linksheet.module.devicecompat.CompatModule
import fe.linksheet.module.devicecompat.miui.MiuiCompatProvider
import fe.linksheet.module.devicecompat.miui.RealMiuiCompatProvider
import fe.linksheet.module.devicecompat.oneui.OneUiCompatProvider
import fe.linksheet.module.devicecompat.oneui.RealOneUiCompatProvider
import fe.linksheet.module.downloader.downloaderModule
import fe.linksheet.module.http.HttpModule
import fe.linksheet.module.log.DefaultLogModule
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.log.file.entry.LogEntryDeserializer
import fe.linksheet.module.network.networkStateServiceModule
import fe.linksheet.module.paste.pasteServiceModule
import fe.linksheet.module.preference.preferenceRepositoryModule
import fe.linksheet.module.preference.state.AppStateServiceModule
import fe.linksheet.module.profile.ProfileSwitcherModule
import fe.linksheet.module.repository.module.repositoryModule
import fe.linksheet.module.resolver.module.resolverModule
import fe.linksheet.module.resolver.urlresolver.amp2html.amp2HtmlResolveRequestModule
import fe.linksheet.module.resolver.urlresolver.base.allRemoteResolveRequest
import fe.linksheet.module.resolver.urlresolver.redirect.redirectResolveRequestModule
import fe.linksheet.module.shizuku.shizukuHandlerModule
import fe.linksheet.module.statistic.statisticsModule
import fe.linksheet.module.systeminfo.SystemInfoServiceModule
import fe.linksheet.module.versiontracker.VersionTrackerModule
import fe.linksheet.module.viewmodel.module.viewModelModule
import fe.linksheet.util.serialization.HttpUrlTypeAdapter
import fe.linksheet.util.serialization.UriTypeAdapter
import kotlinx.coroutines.flow.StateFlow
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
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

        if (AndroidVersion.isAtLeastApi28P() && !Testing.IsTestRunner) {
            HiddenApiBypass.addHiddenApiExemptions("")
        }

        DynamicColors.applyToActivitiesIfAvailable(this)

        val koinModules = provideKoinModules()
        val koinApplication = startKoin {
            androidLogger()
            androidApplicationContext(this@LinkSheetApp)
            applicationLifecycle(lifecycleObserver)
            modules(koinModules)
        }

        lifecycleObserver.onAppInitialized()
    }

    override fun provideKoinModules(): List<Module> {
        return listOf(
            SystemInfoServiceModule,
            PackageModule,
            networkStateServiceModule,
            shizukuHandlerModule,
            globalGsonModule,
            preferenceRepositoryModule,
            DefaultLogModule,
            provideCompatProvider(),
            CompatModule,
            databaseModule,
            daoModule,
            HttpModule,
            redirectResolveRequestModule,
            amp2HtmlResolveRequestModule,
            allRemoteResolveRequest,
            resolverModule,
            repositoryModule,
            viewModelModule,
            downloaderModule,
            provideAnalyticsClient(),
            analyticsServiceModule,
            statisticsModule,
            VersionTrackerModule,
            pasteServiceModule,
            ProfileSwitcherModule,
            AppStateServiceModule,
            provideDebugModule()
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
