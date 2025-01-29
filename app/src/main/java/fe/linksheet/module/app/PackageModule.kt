package fe.linksheet.module.app

import android.content.Context
import fe.linksheet.extension.koin.getSystemServiceOrThrow
import fe.linksheet.module.app.`package`.AndroidPackageIconLoaderModule
import fe.linksheet.module.app.`package`.AndroidPackageIntentHandler
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import org.koin.dsl.module

val PackageModule = module {
    single {
        AndroidPackageIconLoaderModule(get<Context>().packageManager, getSystemServiceOrThrow())
    }
    single {
        AndroidPackageIntentHandler(
            get<Context>().packageManager,
            get<ExperimentRepository>().asState(Experiments.hideReferrerFromSheet)::invoke,
            get<ExperimentRepository>().asState(Experiments.disableDeduplication)::invoke
        )
    }
    single {
        AndroidPackageServiceModule(get(), get())
    }
}
