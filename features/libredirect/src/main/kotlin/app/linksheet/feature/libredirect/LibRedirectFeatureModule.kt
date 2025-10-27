package app.linksheet.feature.libredirect

import app.linksheet.feature.libredirect.database.repository.LibRedirectDefaultRepository
import app.linksheet.feature.libredirect.database.repository.LibRedirectStateRepository
import app.linksheet.feature.libredirect.viewmodel.LibRedirectServiceSettingsViewModel
import app.linksheet.feature.libredirect.viewmodel.LibRedirectSettingsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val LibRedirectFeatureModule = module {
    singleOf(::LibRedirectResolver)
    singleOf(::LibRedirectDefaultRepository)
    singleOf(::LibRedirectStateRepository)
    viewModelOf(::LibRedirectSettingsViewModel)
    viewModel { parameters ->
        LibRedirectServiceSettingsViewModel(
            context = get(),
            serviceKey = parameters.get(),
            defaultRepository = get(),
            stateRepository = get(),
        )
    }
}
