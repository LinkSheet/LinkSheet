package fe.linksheet.module.preference

import app.linksheet.feature.browser.preference.BrowserPreferences
import app.linksheet.feature.libredirect.preference.Experiment
import app.linksheet.feature.libredirect.preference.LibRedirectPreferences
import app.linksheet.feature.profile.preference.ProfilePreferences
import app.linksheet.feature.shizuku.preference.ShizukuPreferences
import fe.composekit.preference.asFunction
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.app.DefaultAppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.preference.flags.FeatureFlagRepository
import fe.linksheet.module.preference.state.AppStateRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.qualifier
import org.koin.dsl.bind
import org.koin.dsl.module

val PreferenceRepositoryModule = module {
    singleOf(::DefaultAppPreferenceRepository).bind<AppPreferenceRepository>()
    singleOf(::FeatureFlagRepository)
    singleOf(::ExperimentRepository)
    singleOf(::AppStateRepository)
    single<ShizukuPreferences> { AppPreferences.shizuku }
    single<LibRedirectPreferences> { AppPreferences.libRedirect }
    single<() -> Boolean>(Experiment.CustomInstances.qualifier) {
        get<ExperimentRepository>().asFunction(Experiments.libRedirectCustomInstances)
    }
    single<BrowserPreferences> { AppPreferences.browser }
    single<ProfilePreferences> { AppPreferences.profileSwitcher }
}
