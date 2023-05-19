package fe.linksheet

import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors
import fe.linksheet.module.dao.daoModule
import fe.linksheet.module.database.databaseModule
import fe.linksheet.module.downloader.downloaderModule
import fe.linksheet.module.preference.preferenceRepositoryModule
import fe.linksheet.module.redirectresolver.redirectResolverModule
import fe.linksheet.module.repository.repositoryModule
import fe.linksheet.module.request.requestModule
import fe.linksheet.module.viewmodel.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber

class LinkSheet : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@LinkSheet)
            modules(
                databaseModule,
                daoModule,
                repositoryModule,
                viewModelModule,
                preferenceRepositoryModule,
                requestModule,
                redirectResolverModule,
                downloaderModule
            )
        }
    }
}