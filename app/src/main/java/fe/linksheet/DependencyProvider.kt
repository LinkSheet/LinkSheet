package fe.linksheet

import org.koin.core.module.Module

interface DependencyProvider {
    fun provideKoinModules(): List<Module>

    fun provideMiuiCompatProvider(): Module
    fun provideAnalyticsClient(): Module
    fun provideDebugMenu(): Module
}
