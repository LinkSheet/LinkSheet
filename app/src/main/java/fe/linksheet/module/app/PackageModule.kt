package fe.linksheet.module.app

import fe.android.preference.helper.compose.asFunction
import fe.linksheet.extension.koin.getPackageManager
import fe.linksheet.extension.koin.getSystemServiceOrThrow
import fe.linksheet.module.app.`package`.AndroidPackageIconLoaderModule
import fe.linksheet.module.app.`package`.AndroidPackageIntentHandler
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import org.koin.dsl.module

val PackageModule = module {
    single {
        AndroidPackageIconLoaderModule(getPackageManager(), getSystemServiceOrThrow())
    }
    single {
        AndroidPackageIntentHandler(
            getPackageManager(),
            get<ExperimentRepository>().asFunction(Experiments.hideReferrerFromSheet),
        )
    }
    single {
        AndroidPackageServiceModule(get(), get())
    }
}
