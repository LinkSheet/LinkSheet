package fe.linksheet.module.app

import fe.composekit.preference.asFunction
import fe.droidkit.koin.getPackageManager
import fe.droidkit.koin.getSystemServiceOrThrow
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
        val experimentRepository = get<ExperimentRepository>()

        AndroidPackageIntentHandler(
            getPackageManager(),
            experimentRepository.asFunction(Experiments.hideReferrerFromSheet)
        )
    }
    single {
        AndroidPackageServiceModule(get(), get(), get())
    }
}
