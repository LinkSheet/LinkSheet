package fe.linksheet.debug

import fe.linksheet.LinkSheetApp
import fe.linksheet.debug.module.debug.RealDebugMenuSlotProvider
import fe.linksheet.debug.module.devicecompat.DebugMiuiCompatProvider
import fe.linksheet.debug.module.viewmodel.module.DebugViewModelModule
import fe.linksheet.module.analytics.client.DebugLogAnalyticsClient
import fe.linksheet.module.analytics.client.aptabaseAnalyticsClientModule
import fe.linksheet.module.debug.DebugMenuSlotProvider
import fe.linksheet.module.devicecompat.miui.MiuiCompatProvider
import fe.linksheet.module.devicecompat.samsung.RealSamsungIntentCompatProvider
import fe.linksheet.module.devicecompat.samsung.SamsungIntentCompatProvider
import fe.linksheet.util.buildconfig.BuildType
import org.koin.core.module.Module
import org.koin.dsl.module

class DebugLinkSheetApp : LinkSheetApp() {
    override fun provideKoinModules(): List<Module> {
        return super.provideKoinModules() + DebugViewModelModule
    }

    override fun provideCompatProvider(): Module {
        return module {
            single<MiuiCompatProvider> { DebugMiuiCompatProvider }
            single<SamsungIntentCompatProvider> { RealSamsungIntentCompatProvider(get()) }
        }
    }

    override fun provideAnalyticsClient(): Module = when {
        BuildType.current.isTestRunner -> DebugLogAnalyticsClient.module
        else -> aptabaseAnalyticsClientModule
    }

    override fun provideDebugMenu(): Module {
        return module {
            single<DebugMenuSlotProvider> { RealDebugMenuSlotProvider(get()) }
        }
    }
}
