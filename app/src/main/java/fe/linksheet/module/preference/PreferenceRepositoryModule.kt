package fe.linksheet.module.preference

import fe.android.preference.helper.PreferenceRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val preferenceRepositoryModule = module {
    single { PreferenceRepository(get()) }
}

val featureFlagRepositoryModule = module {
    single(named("feature_flags")) { PreferenceRepository(get(), "feature_flags") }
}