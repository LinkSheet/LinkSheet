package fe.linksheet.module.preference

import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.flags.FeatureFlagRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val preferenceRepositoryModule = module {
    singleOf(::AppPreferenceRepository)
    singleOf(::FeatureFlagRepository)
    singleOf(::ExperimentRepository)
}
