package fe.linksheet.module.preference

import fe.android.preference.helper.PreferenceRepository
import fe.linksheet.module.viewmodel.FeatureFlagViewModel
import org.koin.dsl.module

val preferenceRepositoryModule = module {
    single { PreferenceRepository(get()) }
}

val featureFlagRepositoryModule = module {
    single(FeatureFlagViewModel.featureFlagNamed) { PreferenceRepository(get(), FeatureFlagViewModel.featureFlagName) }
}