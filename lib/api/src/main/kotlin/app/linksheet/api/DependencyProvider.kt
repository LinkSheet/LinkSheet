package app.linksheet.api

import app.linksheet.api.preference.AppPreferenceRepository
import fe.android.preference.helper.PreferenceRepository
import org.koin.core.module.Module

interface DependencyProvider {
    fun provideKoinModules(preferenceRepository: AppPreferenceRepository): List<Module>
    fun provideCompatProvider(): Module
    fun provideAnalyticsClient(): Module
    fun provideDebugModule(): Module
    fun provideAppModule(): Module
}
