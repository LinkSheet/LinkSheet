package fe.linksheet.debug

import fe.linksheet.LinkSheetApp
import fe.linksheet.debug.module.debug.RealDebugMenuSlotProvider
import fe.linksheet.debug.module.devicecompat.DebugMiuiCompatProvider
import fe.linksheet.debug.module.viewmodel.module.DebugViewModelModule
import fe.linksheet.module.analytics.client.aptabaseAnalyticsClientModule
import fe.linksheet.module.debug.DebugMenuSlotProvider
import fe.linksheet.module.devicecompat.MiuiCompatProvider
import org.koin.core.module.Module
import org.koin.dsl.module

class DebugLinkSheetApp : LinkSheetApp() {
    override fun provideKoinModules(): List<Module> {
        return super.provideKoinModules() + DebugViewModelModule
    }

    override fun provideMiuiCompatProvider(): Module {
        return module {
            single<MiuiCompatProvider> { DebugMiuiCompatProvider }
        }
    }

    override fun provideAnalyticsClient(): Module {
        return aptabaseAnalyticsClientModule
    }

    override fun provideDebugMenu(): Module {
        return module {
            single<DebugMenuSlotProvider> { RealDebugMenuSlotProvider(get()) }
        }
    }
}
