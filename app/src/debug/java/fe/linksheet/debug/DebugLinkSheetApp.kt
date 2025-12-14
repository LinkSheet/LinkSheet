package fe.linksheet.debug

import android.os.StrictMode
import app.linksheet.compose.debug.DebugMenuSlotProvider
import app.linksheet.compose.debug.DebugPreferenceProvider
import app.linksheet.feature.devicecompat.miui.MiuiCompatProvider
import app.linksheet.feature.devicecompat.oneui.OneUiCompatProvider
import app.linksheet.feature.devicecompat.oneui.RealOneUiCompatProvider
import app.linksheet.testing.Testing
import fe.linksheet.LinkSheetApp
import fe.linksheet.debug.module.debug.RealDebugMenuSlotProvider
import fe.linksheet.debug.module.debug.RealDebugPreferenceProvider
import fe.linksheet.debug.module.devicecompat.DebugMiuiCompatProvider
import fe.linksheet.debug.module.preference.DebugPreferenceRepository
import fe.linksheet.debug.module.viewmodel.module.DebugViewModelModule
import fe.linksheet.module.analytics.client.DebugLogAnalyticsClient
import fe.linksheet.module.analytics.client.aptabaseAnalyticsClientModule
import org.koin.core.module.Module
import org.koin.dsl.module

class DebugLinkSheetApp : LinkSheetApp() {
    override fun onCreate() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
//                .penaltyDeath()
                .build()
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
//                .penaltyDeath()
                .build()
        )
        super.onCreate()
    }

    override fun provideKoinModules(): List<Module> {
        return super.provideKoinModules() + DebugViewModelModule
    }

    override fun provideCompatProvider(): Module {
        return module {
            single<MiuiCompatProvider> { DebugMiuiCompatProvider(get()) }
            single<OneUiCompatProvider> { RealOneUiCompatProvider(get()) }
        }
    }

    override fun provideAnalyticsClient(): Module = when {
        Testing.IsTestRunner -> DebugLogAnalyticsClient.module
        else -> aptabaseAnalyticsClientModule
    }

    override fun provideDebugModule(): Module {
        return module {
            single<DebugPreferenceRepository> { DebugPreferenceRepository(get()) }
            single<DebugMenuSlotProvider> { RealDebugMenuSlotProvider(get()) }
            single<DebugPreferenceProvider> { RealDebugPreferenceProvider(get()) }
        }
    }
}
