package fe.linksheet

import org.koin.core.module.Module

interface DependencyProvider {
    fun provideKoinModules(): List<Module>

    fun provideCompatProvider(): Module
    fun provideAnalyticsClient(): Module
    fun provideDebugMenu(): Module
}
