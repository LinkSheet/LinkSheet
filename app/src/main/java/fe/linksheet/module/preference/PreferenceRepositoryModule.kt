package fe.linksheet.module.preference

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val preferenceRepositoryModule = module {
    singleOf(::AppPreferenceRepository)
    singleOf(::FeatureFlagRepository)
}
