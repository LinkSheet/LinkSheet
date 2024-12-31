package fe.linksheet.module.app

import android.content.Context
import fe.linksheet.extension.koin.getSystemServiceOrThrow
import org.koin.dsl.module

val PackageModule = module {
    single {
        PackageIconLoaderModule(get<Context>().packageManager, getSystemServiceOrThrow())
    }
    single {
        AndroidPackageInfoModule(get(), get())
    }
}
