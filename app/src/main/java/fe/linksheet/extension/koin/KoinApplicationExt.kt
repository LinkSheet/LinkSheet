package fe.linksheet.extension.koin

import android.app.Application
import android.content.Context
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.logger.Level
import org.koin.dsl.binds
import org.koin.dsl.module

@OptIn(KoinInternalApi::class)
inline fun <reified T> KoinApplication.androidApplicationContext(application: Application): KoinApplication {
    if (koin.logger.isAt(Level.INFO)) {
        koin.logger.info("[init] declare Android Context")
    }

    koin.loadModules(listOf(module {
        single { application } binds arrayOf(Context::class, Application::class, T::class)
    }))

    return this
}
