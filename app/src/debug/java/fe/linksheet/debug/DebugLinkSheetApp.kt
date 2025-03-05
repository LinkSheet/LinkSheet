package fe.linksheet.debug

import android.os.StrictMode
import app.linksheet.testing.Testing
import fe.linksheet.LinkSheetApp
import fe.linksheet.debug.module.debug.RealDebugMenuSlotProvider
import fe.linksheet.debug.module.devicecompat.DebugMiuiCompatProvider
import fe.linksheet.debug.module.viewmodel.module.DebugViewModelModule
import fe.linksheet.module.analytics.client.DebugLogAnalyticsClient
import fe.linksheet.module.analytics.client.aptabaseAnalyticsClientModule
import fe.linksheet.module.debug.DebugMenuSlotProvider
import fe.linksheet.module.devicecompat.miui.MiuiCompatProvider
import fe.linksheet.module.devicecompat.oneui.RealOneUiCompatProvider
import fe.linksheet.module.devicecompat.oneui.OneUiCompatProvider
import org.koin.core.module.Module
import org.koin.dsl.module

class DebugLinkSheetApp : LinkSheetApp() {
    override fun onCreate() {
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().build())
        super.onCreate()
    }

    override fun provideKoinModules(): List<Module> {
        return super.provideKoinModules() + DebugViewModelModule
    }

    override fun provideCompatProvider(): Module {
        return module {
            single<MiuiCompatProvider> { DebugMiuiCompatProvider }
            single<OneUiCompatProvider> { RealOneUiCompatProvider(get()) }
        }
    }

    override fun provideAnalyticsClient(): Module = when {
        Testing.IsTestRunner -> DebugLogAnalyticsClient.module
        else -> aptabaseAnalyticsClientModule
    }

    override fun provideDebugMenu(): Module {
        return module {
            single<DebugMenuSlotProvider> { RealDebugMenuSlotProvider(get()) }
        }
    }
}
